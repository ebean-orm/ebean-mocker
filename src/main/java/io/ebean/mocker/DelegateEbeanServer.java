package io.ebean.mocker;

import io.ebean.*;
import io.ebean.annotation.Platform;
import io.ebean.annotation.TxIsolation;
import io.ebean.bean.BeanCollection;
import io.ebean.bean.BeanLoader;
import io.ebean.bean.CallOrigin;
import io.ebean.bean.EntityBeanIntercept;
import io.ebean.cache.ServerCacheManager;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.DatabasePlatform;
import io.ebean.event.readaudit.ReadAuditLogger;
import io.ebean.event.readaudit.ReadAuditPrepare;
import io.ebean.meta.MetaInfoManager;
import io.ebean.meta.MetricVisitor;
import io.ebean.mocker.backgroundexecutor.ImmediateBackgroundExecutor;
import io.ebean.mocker.delegate.*;
import io.ebean.plugin.BeanType;
import io.ebean.plugin.Property;
import io.ebean.plugin.SpiServer;
import io.ebean.text.csv.CsvReader;
import io.ebean.text.json.JsonContext;
import io.ebeaninternal.api.*;
import io.ebeaninternal.server.core.SpiResultSet;
import io.ebeaninternal.server.core.timezone.DataTimeZone;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.query.CQuery;
import io.ebeaninternal.server.transaction.RemoteTransactionEvent;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.time.Clock;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer, for example
 * only overwrites some of the find or save functionality but leaves the rest of the
 * behavior to be handled normally by the underlying delegate.
 * <p>
 * The underlying delegate is most often a fully functional EbeanServer that is using H2
 * as a test database.
 * </p>
 */
public class DelegateEbeanServer implements SpiEbeanServer, DelegateAwareEbeanServer, DelegateMethodNames {

  /**
   * The list of methods calls made to this server.
   */
  public MethodCalls methodCalls = new MethodCalls();

  /**
   * The beans sent to the save(), delete() methods etc.
   */
  public BeanCapture capturedBeans = new BeanCapture();

  /**
   * Various find methods that have specific test responses.
   */
  protected WhenFind whenFind = new WhenFind();

  /**
   * Test double replacements for 'Finders' which are static fields on entity beans.
   */
  protected WithStaticFinders withStaticFinders = new WithStaticFinders();

  /**
   * The underlying EbeanServer we delegate to.
   * <p/>
   * This will often be a fully functional EbeanSever that uses H2.
   */
  protected Database delegate;

  /**
   * Expect ImmediateBackgroundExecutor to be a good default. Can use IgnoreBackgroundExecutor or the delegates one.
   */
  protected BackgroundExecutor backgroundExecutor = new ImmediateBackgroundExecutor();

  /**
   * Constructs queries that will call back to this so not really expecting it to be overwritten.
   */
  protected DelegateQuery delegateQuery;

  protected InterceptSave save;

  protected InterceptBulkUpdate bulkUpdate;

  protected InterceptDelete delete;

  protected InterceptFind find;

  protected InterceptPublish publish;

  protected InterceptFindSqlQuery findSqlQuery;

  /**
   * If set to true the 'bulk update' calls are passed through to the underlying delegate.
   */
  protected boolean persistBulkUpdates;

  /**
   * If set to true the 'delete' calls are passed through to the underlying delegate.
   */
  protected boolean persistDeletes;

  /**
   * If set to true the 'insert' calls are passed through to the underlying delegate.
   */
  protected boolean persistInserts;

  /**
   * If set to true the 'save' calls are passed through to the underlying delegate.
   */
  protected boolean persistSaves;

  /**
   * If set to true the 'update' calls are passed through to the underlying delegate.
   */
  protected boolean persistUpdates;

  /**
   * If set to true the publish/draft calls are passed through to the underlying delegate.
   */
  protected boolean persistPublish;

  /**
   * Construct with defaults.
   */
  public DelegateEbeanServer() {

  }

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateEbeanServer withDelegate(Database delegate) {
    this.delegate = delegate;
    this.delegateQuery = new DelegateQuery(delegate, this);
    this.save = new DelegateSave(delegate);
    this.delete = new DelegateDelete(delegate);
    this.bulkUpdate = new DelegateBulkUpdate(delegate);
    this.find = new DelegateFind(delegate);
    this.findSqlQuery = new DelegateFindSqlQuery(delegate);
    this.publish = new DelegatePublish(delegate);
    return this;
  }

  @Override
  public void beforeRun() {
    withStaticFinders.beforeRun();
  }

  @Override
  public void afterRun() {
    withStaticFinders.afterRun();
  }

  public WhenFind whenFind() {
    return whenFind;
  }

  /**
   * Used to specify a test double to replace a static 'finder' field on the given beanType.
   * <pre>{@code
   *
   *   DelegateEbeanServer mock = new DelegateEbeanServer();
   *   mock.withFinder(Customer.class).as(new TDCustomerFinder());
   *
   *   // Note: TDCustomerFinder not set onto Customer until runWithMock()
   *
   *   MockiEbean.runWithMock(mock, new Runnable() {
   *
   *     public void run() {
   *       ...
   *       // Customer.find now is our test double TDCustomerFinder
   *       Customer found = Customer.find.byUniqueName("foo");
   *     }
   *   });
   *
   *   // Note: original Customer.find implementation is restored by MockiEbean
   *
   * }</pre>
   */
  public <T> WithStaticFinder<T> withFinder(Class<T> beanType) {
    return withStaticFinders.withFinder(beanType);
  }


  /**
   * Set the underlying delegate to proxy requests to.
   * <p/>
   * Used with the default constructor such that this DelegateEbeanServer
   * can be setup prior to having access to the underlying EbeanServer
   * that we want to proxy through to.
   * <p/>
   * Return true if the underling ebeanServer was set.
   */
  public boolean withDelegateIfRequired(Database delegate) {
    if (this.delegate == null) {
      withDelegate(delegate);
      return true;
    }
    if (this.delegate instanceof DelegateAwareEbeanServer) {
      // probably using ProxyEbeanServer to capture method calls etc
      return ((DelegateAwareEbeanServer) this.delegate).withDelegateIfRequired(delegate);
    }
    // delegate was not set
    return false;
  }

  public DelegateEbeanServer withInterceptFind(InterceptFind find) {
    this.find = find;
    return this;
  }

  /**
   * Set to true for all the persisting calls skip/avoid calling the underlying delegate.
   * <p>
   * So when set to true then all the calls to save(), delete() etc do not get passed on the
   * the underlying delegate.
   */
  public DelegateEbeanServer withPersisting(boolean persisting) {
    persistBulkUpdates = persisting;
    persistDeletes = persisting;
    persistInserts = persisting;
    persistUpdates = persisting;
    persistSaves = persisting;
    return this;
  }

  @Override
  public SpiServer pluginApi() {
    methodCalls.add(MethodCall.of("pluginApi"));
    return delegate.pluginApi();
  }

  @Override
  public AutoTune autoTune() {
    methodCalls.add(MethodCall.of("autoTune"));
    return delegate.autoTune();
  }

  @Override
  public DocumentStore docStore() {
    methodCalls.add(MethodCall.of("docStore"));
    return delegate.docStore();
  }

  @Override
  public Platform platform() {
    methodCalls.add(MethodCall.of("platform"));
    return delegate.platform();
  }

  @Override
  public ScriptRunner script() {
    methodCalls.add(MethodCall.of("script"));
    return delegate.script();
  }

  @Override
  public DataSource dataSource() {
    methodCalls.add(MethodCall.of("dataSource"));
    return delegate.dataSource();
  }

  @Override
  public DataSource readOnlyDataSource() {
    methodCalls.add(MethodCall.of("readOnlyDataSource"));
    return delegate.readOnlyDataSource();
  }

  @Override
  public boolean isDisableL2Cache() {
    methodCalls.add(MethodCall.of("isDisableL2Cache"));
    return spiDelegate().isDisableL2Cache();
  }

  @Override
  public void clearServerTransaction() {
    methodCalls.add(MethodCall.of("clearServerTransaction"));
    spiDelegate().clearServerTransaction();
  }

  @Override
  public SpiTransaction createReadOnlyTransaction(Object tenantId) {
    methodCalls.add(MethodCall.of("createReadOnlyTransaction"));
    return spiDelegate().createReadOnlyTransaction(tenantId);
  }

  @Override
  public SpiQueryBindCapture createQueryBindCapture(SpiQueryPlan queryPlan) {
    methodCalls.add(MethodCall.of("createQueryBindCapture"));
    return spiDelegate().createQueryBindCapture(queryPlan);
  }

  @Override
  public void truncate(String... tables) {
    methodCalls.add(MethodCall.of("truncate"));
    delegate.truncate(tables);
  }

  @Override
  public void truncate(Class<?>... tables) {
    methodCalls.add(MethodCall.of("truncate"));
    delegate.truncate(tables);
  }

  /**
   * Return the BackgroundExecutor.
   * <p>
   * Typically for testing we either want these to run immediately or not at all.
   * Defaults to use ImmediateBackgroundExecutor, use IgnoreBackgroundExecutor if desired.
   */
  @Override
  public BackgroundExecutor backgroundExecutor() {
    methodCalls.add(MethodCall.of("backgroundExecutor"));
    return backgroundExecutor;
  }

  @Override
  public ServerCacheManager cacheManager() {
    methodCalls.add(MethodCall.of("cacheManager"));
    return delegate.cacheManager();
  }

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {
    methodCalls.add(MethodCall.of("shutdown").with("shutdownDataSource", shutdownDataSource, "deregisterDriver", deregisterDriver));
    delegate.shutdown(shutdownDataSource, deregisterDriver);
  }

  @Override
  public JsonContext json() {
    methodCalls.add(MethodCall.of("json"));
    return delegate.json();
  }

  @Override
  public String name() {
    methodCalls.add(MethodCall.of("name"));
    return delegate.name();
  }

  @Override
  public ExpressionFactory expressionFactory() {
    methodCalls.add(MethodCall.of("expressionFactory"));
    return delegate.expressionFactory();
  }

  @Override
  public MetaInfoManager metaInfo() {
    methodCalls.add(MethodCall.of("metaInfo"));
    return delegate.metaInfo();
  }

  @Override
  public BeanState beanState(Object bean) {
    methodCalls.add(MethodCall.of("beanState").with("bean", bean));
    return delegate.beanState(bean);
  }

  @Override
  public Object beanId(Object bean) {
    methodCalls.add(MethodCall.of("beanId").with("bean", bean));
    return delegate.beanId(bean);
  }

  @Override
  public Object beanId(Object bean, Object id) {
    methodCalls.add(MethodCall.of("beanId").with("bean", bean).with("id", id));
    return delegate.beanId(bean, id);
  }

  @Override
  public Map<String, ValuePair> diff(Object a, Object b) {
    methodCalls.add(MethodCall.of("diff").with("a", a, "b", b));
    return delegate.diff(a, b);
  }

  @Override
  public <T> T createEntityBean(Class<T> beanType) {
    methodCalls.add(MethodCall.of("createEntityBean").with("beanType", beanType));
    return delegate.createEntityBean(beanType);
  }

  @Override
  public <T> CsvReader<T> createCsvReader(Class<T> beanType) {
    methodCalls.add(MethodCall.of("createCsvReader").with("beanType", beanType));
    return delegate.createCsvReader(beanType);
  }

  @Override
  public <T> Filter<T> filter(Class<T> beanType) {
    methodCalls.add(MethodCall.of("filter").with("beanType", beanType));
    return delegate.filter(beanType);
  }

  @Override
  public <T> void sort(List<T> list, String sortByClause) {
    methodCalls.add(MethodCall.of("sort").with("list", list, "sortByClause", sortByClause));
    delegate.sort(list, sortByClause);
  }

  @Override
  public void markAsDirty(Object bean) {
    methodCalls.add(MethodCall.of("markAsDirty").with("bean", bean));
    delegate.markAsDirty(bean);
  }

  // -- create updates ------------------------

  @Override
  public <T> Update<T> createUpdate(Class<T> beanType, String ormUpdate) {
    methodCalls.add(MethodCall.of("createUpdate").with("beanType", beanType, "ormUpdate", ormUpdate));
    return delegate.createUpdate(beanType, ormUpdate);
  }

  @Override
  public SqlUpdate sqlUpdate(String sql) {
    methodCalls.add(MethodCall.of("sqlUpdate").with("sql", sql));
    return delegate.sqlUpdate(sql);
  }

  @Override
  public CallableSql createCallableSql(String callableSql) {
    methodCalls.add(MethodCall.of("createCallableSql").with("callableSql", callableSql));
    return delegate.createCallableSql(callableSql);
  }

  // -- transaction ------------------------

  @Override
  public void execute(TxScope scope, Runnable runnable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("scope", scope, "runnable", runnable));
    delegate.execute(scope, runnable);
  }

  @Override
  public void execute(Runnable runnable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("runnable", runnable));
    delegate.execute(runnable);
  }

  @Override
  public <T> T executeCall(TxScope scope, Callable<T> callable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("scope", scope, "callable", callable));
    return delegate.executeCall(scope, callable);
  }

  @Override
  public <T> T executeCall(Callable<T> callable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("callable", callable));
    return delegate.executeCall(callable);
  }

  @Override
  public void register(TransactionCallback transactionCallback) throws PersistenceException {
    methodCalls.add(MethodCall.of("register").with("transactionCallback", transactionCallback));
    delegate.register(transactionCallback);
  }

  @Override
  public Transaction createTransaction() {
    methodCalls.add(MethodCall.of("createTransaction"));
    return delegate.createTransaction();
  }

  @Override
  public Transaction createTransaction(TxIsolation isolation) {
    methodCalls.add(MethodCall.of("createTransaction").with("isolation", isolation));
    return delegate.createTransaction(isolation);
  }

  @Override
  public Transaction beginTransaction() {
    methodCalls.add(MethodCall.of("beginTransaction"));
    return delegate.beginTransaction();
  }

  @Override
  public Transaction beginTransaction(TxIsolation isolation) {
    methodCalls.add(MethodCall.of("beginTransaction").with("isolation", isolation));
    return delegate.beginTransaction(isolation);
  }

  @Override
  public Transaction beginTransaction(TxScope scope) {
    methodCalls.add(MethodCall.of("beginTransaction").with("scope", scope));
    return delegate.beginTransaction(scope);
  }

  @Override
  public Transaction currentTransaction() {
    methodCalls.add(MethodCall.of("currentTransaction"));
    return delegate.currentTransaction();
  }

  @Override
  public void flush() {
    methodCalls.add(MethodCall.of("flush"));
    delegate.flush();
  }

  @Override
  public void commitTransaction() {
    methodCalls.add(MethodCall.of("commitTransaction"));
    delegate.commitTransaction();
  }

  @Override
  public void rollbackTransaction() {
    methodCalls.add(MethodCall.of("rollbackTransaction"));
    delegate.rollbackTransaction();
  }

  @Override
  public void endTransaction() {
    methodCalls.add(MethodCall.of("endTransaction"));
    delegate.endTransaction();
  }

  // -- delegateQuery ------------------------

  @Override
  public <T> T reference(Class<T> beanType, Object id) {
    methodCalls.add(MethodCall.of("reference").with("beanType", beanType, "id", id));
    return delegateQuery.reference(beanType, id);
  }

  @Override
  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    methodCalls.add(MethodCall.of("createNamedQuery").with("beanType", beanType).with("namedQuery", namedQuery));
    return delegateQuery.createNamedQuery(beanType, namedQuery);
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType, String eql) {
    methodCalls.add(MethodCall.of("createQuery").with("beanType", beanType).with("eql", eql));
    return delegateQuery.createQuery(beanType, eql);
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType) {
    methodCalls.add(MethodCall.of("createQuery").with("beanType", beanType));
    return delegateQuery.createQuery(beanType);
  }

  @Override
  public <T> Set<String> validateQuery(Query<T> query) {
    methodCalls.add(MethodCall.of("validateQuery").with("query", query));
    return delegateQuery.validateQuery(query);
  }

  @Override
  public void lock(Object o) {

  }

  @Override
  public <T> DtoQuery<T> findDto(Class<T> dtoType, String sql) {
    return delegateQuery.findDto(dtoType, sql);
  }

  @Override
  public <T> DtoQuery<T> createNamedDtoQuery(Class<T> dtoType, String namedQuery) {
    return delegateQuery.createNamedDtoQuery(dtoType, namedQuery);
  }

  @Override
  public <T> Query<T> find(Class<T> beanType) {
    methodCalls.add(MethodCall.of("find").with("beanType", beanType));
    return delegateQuery.find(beanType);
  }

  @Override
  public <T> Query<T> findNative(Class<T> beanType, String nativeSql) {
    methodCalls.add(MethodCall.of("findNative").with("beanType", beanType).with("nativeSql", nativeSql));
    return delegateQuery.findNative(beanType, nativeSql);
  }

  @Override
  public SqlQuery sqlQuery(String sql) {
    methodCalls.add(MethodCall.of("sqlQuery").with("sql", sql));
    return delegateQuery.sqlQuery(sql);
  }

  // -- refresh ------------------------

  @Override
  public void refresh(Object bean) {
    methodCalls.add(MethodCall.of("refresh").with("bean", bean));
    find.refresh(bean);
  }

  @Override
  public void refreshMany(Object bean, String propertyName) {
    methodCalls.add(MethodCall.of("refreshMany").with("bean", bean, "propertyName", propertyName));
    find.refreshMany(bean, propertyName);
  }

  // -- find ------------------------

  @Override
  public <T> T find(Class<T> beanType, Object id) {
    methodCalls.add(MethodCall.of("find").with("beanType", beanType, "id", id));
    WhenBeanReturn match = whenFind.findMatchById(beanType, id);
    if (match != null) {
      return (T) match.val();
    }
    return find.find(beanType, id, null);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id, Transaction transaction) {
    methodCalls.add(MethodCall.of("find").with("beanType", beanType, "id", id, "transaction", transaction));
    WhenBeanReturn<T> match = whenFind.findMatchById(beanType, id);
    if (match != null) {
      return match.val();
    }
    return find.find(beanType, id, transaction);
  }

  @Override
  public <T> T findOne(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findOne").with("query", query, "transaction", transaction));
    WhenBeanReturn<T> match = whenFind.findMatchByUnique(((SpiQuery<T>) query).getBeanType());
    if (match != null) {
      return match.val();
    }
    return find.findOne(query, transaction);
  }

  @Override
  public <T> Optional<T> findOneOrEmpty(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findOneOrEmpty").with("query", query, "transaction", transaction));
    WhenBeanReturn<T> match = whenFind.findMatchByUnique(((SpiQuery<T>) query).getBeanType());
    if (match != null) {
      return Optional.ofNullable(match.val());
    }
    return Optional.ofNullable(find.findOne(query, transaction));
  }

  @Override
  public <T> int findCount(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findCount").with("query", query, "transaction", transaction));
    return find.findCount(query, transaction);
  }

  @Override
  public <A, T> List<A> findIds(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findIds").with("query", query, "transaction", transaction));
    return find.findIds(query, transaction);
  }

  @Override
  public <A, T> List<A> findSingleAttributeList(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findSingleAttributeList").with("query", query, "transaction", transaction));
    return find.findSingleAttributeList(query, transaction);
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findIterate").with("query", query, "transaction", transaction));
    return find.findIterate(query, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, Consumer<T> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEach").with("query", query, "consumer", consumer, "transaction", transaction));
    find.findEach(query, consumer, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, int i, Consumer<List<T>> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEach").with("query", query, "i", i, "consumer", consumer, "transaction", transaction));
    find.findEach(query, i, consumer, transaction);
  }

  @Override
  public <T> void findEachWhile(Query<T> query, Predicate<T> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEachWhile").with("query", query, "consumer", consumer, "transaction", transaction));
    find.findEachWhile(query, consumer, transaction);
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findList").with("query", query, "transaction", transaction));
    return find.findList(query, transaction);
  }

  @Override
  public <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findFutureCount").with("query", query, "transaction", transaction));
    return find.findFutureCount(query, transaction);
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findFutureIds").with("query", query, "transaction", transaction));
    return find.findFutureIds(query, transaction);
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findFutureList").with("query", query, "transaction", transaction));
    return find.findFutureList(query, transaction);
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findPagedList").with("query", query, "transaction", transaction));
    return find.findPagedList(query, transaction);
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findSet").with("query", query, "transaction", transaction));
    return find.findSet(query, transaction);
  }

  @Override
  public <K, T> Map<K, T> findMap(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findMap").with("query", query, "transaction", transaction));
    return find.findMap(query, transaction);
  }

  @Override
  public <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findVersions").with("query", query, "transaction", transaction));
    return find.findVersions(query, transaction);
  }

  // -- find SqlQuery ------------------------

  @Override
  public List<SqlRow> findList(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findList").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findList(sqlQuery, transaction);
  }

  @Override
  public SqlRow findOne(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findOne").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findOne(sqlQuery, transaction);
  }

  @Override
  public void findEach(SqlQuery sqlQuery, Consumer<SqlRow> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEach").with("sqlQuery", sqlQuery, "consumer", consumer, "transaction", transaction));
    findSqlQuery.findEach(sqlQuery, consumer, transaction);
  }

  @Override
  public void findEachWhile(SqlQuery sqlQuery, Predicate<SqlRow> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEachWhile").with("sqlQuery", sqlQuery, "consumer", consumer, "transaction", transaction));
    findSqlQuery.findEachWhile(sqlQuery, consumer, transaction);
  }

  @Override
  public <T> boolean exists(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("exists").with("query", query, "transaction", transaction));
    return find.exists(query, transaction);
  }

  @Override
  public <T> Stream<T> findStream(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findStream").with("query", query, "transaction", transaction));
    return find.findStream(query, transaction);
  }

  // -- save ------------------------

  @Override
  public Object nextId(Class<?> beanType) {
    methodCalls.add(MethodCall.of("nextId").with("beanType", beanType));
    return !persistSaves ? 0 : save.nextId(beanType);
  }

  @Override
  public Set<Property> checkUniqueness(Object bean) {
    methodCalls.add(MethodCall.of("checkUniqueness").with("bean", bean));
    return delegate.checkUniqueness(bean);
  }

  @Override
  public Set<Property> checkUniqueness(Object bean, Transaction transaction) {
    methodCalls.add(MethodCall.of("checkUniqueness").with("bean", bean));
    return delegate.checkUniqueness(bean, transaction);
  }

  @Override
  public void merge(Object bean) {
    methodCalls.add(MethodCall.of(MERGE).with("bean", bean));
    capturedBeans.addMerged(bean);
    if (persistSaves) {
      save.merge(bean, null, null);
    }
  }

  @Override
  public void merge(Object bean, MergeOptions options) {
    methodCalls.add(MethodCall.of(MERGE).with("bean", bean));
    capturedBeans.addMerged(bean);
    if (persistSaves) {
      save.merge(bean, options, null);
    }
  }

  @Override
  public void merge(Object bean, MergeOptions options, Transaction transaction) {
    methodCalls.add(MethodCall.of(MERGE).with("bean", bean));
    capturedBeans.addMerged(bean);
    if (persistSaves) {
      save.merge(bean, options, transaction);
    }
  }

  @Override
  public void save(Object bean) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(SAVE).with("bean", bean));
    capturedBeans.addSaved(bean);
    if (persistSaves) {
      save.save(bean, null);
    }
  }

  @Override
  public int saveAll(Object... beans) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(SAVE_ALL).with("beans", beans));
    capturedBeans.addSavedAll(Arrays.asList(beans));
    return !persistSaves ? 0 : save.saveAll(Arrays.asList(beans), null);
  }

  @Override
  public int saveAll(Collection<?> beans) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(SAVE_ALL).with("beans", beans));
    capturedBeans.addSavedAll(beans);
    return !persistSaves ? 0 : save.saveAll(beans, null);
  }

  @Override
  public void save(Object bean, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(SAVE).with("bean", bean, "transaction", transaction));
    capturedBeans.addSaved(bean);
    if (persistSaves) {
      save.save(bean, transaction);
    }
  }

  @Override
  public int saveAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(SAVE_ALL).with("beans", beans, "transaction", transaction));
    capturedBeans.addSavedAll(beans);
    return !persistSaves ? 0 : save.saveAll(beans, transaction);
  }

  @Override
  public <T> UpdateQuery<T> update(Class<T> beanType) {
    methodCalls.add(MethodCall.of(UPDATE).with("beanType", beanType));
    return delegate.update(beanType);
  }

  @Override
  public <T> int update(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of(UPDATE).with("query", query).with("transaction", transaction));
    if (persistUpdates) {
      return delegate.extended().update(query, transaction);
    }
    return 0;
  }

  @Override
  public void update(Object bean) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(UPDATE).with("bean", bean));
    capturedBeans.addUpdated(bean);
    if (persistUpdates) {
      save.update(bean, null);
    }
  }

  @Override
  public void update(Object bean, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(UPDATE).with("bean", bean, "transaction", transaction));
    capturedBeans.addUpdated(bean);
    if (persistUpdates) {
      save.update(bean, transaction);
    }
  }

//  @Override
//  public void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException {
//    methodCalls.add(MethodCall.of(UPDATE).with("bean", bean, "transaction", transaction, "deleteMissingChildren", deleteMissingChildren));
//    capturedBeans.addUpdated(bean);
//    if (persistUpdates) {
//      save.update(bean, transaction, deleteMissingChildren);
//    }
//  }

  @Override
  public void updateAll(Collection<?> beans) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(UPDATE_ALL).with("beans", beans));
    capturedBeans.addUpdatedAll(beans);
    if (persistUpdates) {
      save.updateAll(beans, null);
    }
  }

  @Override
  public void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(UPDATE_ALL).with("beans", beans, "transaction", transaction));
    capturedBeans.addUpdatedAll(beans);
    if (persistUpdates) {
      save.updateAll(beans, transaction);
    }
  }

  @Override
  public void insert(Object bean) {
    methodCalls.add(MethodCall.of(INSERT).with("bean", bean));
    capturedBeans.addInserted(bean);
    if (persistInserts) {
      save.insert(bean, null);
    }
  }

  @Override
  public void insert(Object bean, Transaction transaction) {
    methodCalls.add(MethodCall.of(INSERT).with("bean", bean, "transaction", transaction));
    capturedBeans.addInserted(bean);
    if (persistInserts) {
      save.insert(bean, transaction);
    }
  }

  @Override
  public void insertAll(Collection<?> beans) {
    methodCalls.add(MethodCall.of(INSERT_ALL).with("beans", beans));
    capturedBeans.addInsertedAll(beans);
    if (persistInserts) {
      save.insertAll(beans, null);
    }
  }

  @Override
  public void insertAll(Collection<?> beans, Transaction transaction) {
    methodCalls.add(MethodCall.of(INSERT_ALL).with("beans", beans, "transaction", transaction));
    capturedBeans.addInsertedAll(beans);
    if (persistInserts) {
      save.insertAll(beans, transaction);
    }
  }


  // -- delete ------------------------


  @Override
  public boolean delete(Object bean) throws OptimisticLockException {
    methodCalls.add(MethodCall.of("bean").with("bean", bean));
    capturedBeans.addDeleted(bean);
    if (persistDeletes) {
      return delete.delete(bean, null);
    }
    return true;
  }

  @Override
  public int deleteAll(Collection<?> beans) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_ALL).with("beans", beans));
    capturedBeans.addDeletedAll(beans);
    return !persistDeletes ? 0 : delete.deleteAll(beans);
  }

  @Override
  public int deleteAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_ALL).with("beans", beans));
    capturedBeans.addDeletedAll(beans);
    return !persistDeletes ? 0 : delete.deleteAll(beans, transaction);
  }

  @Override
  public boolean deletePermanent(Object bean) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_PERMANENT).with("bean", bean));
    capturedBeans.addDeletePermanent(bean);
    return !persistDeletes ? true : delete.deletePermanent(bean);
  }

  @Override
  public boolean deletePermanent(Object bean, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_PERMANENT).with("bean", bean));
    capturedBeans.addDeletePermanent(bean);
    return !persistDeletes ? true : delete.deletePermanent(bean, transaction);
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_ALL_PERMANENT).with("beans", beans));
    capturedBeans.addDeletedAllPermanent(beans);
    return !persistDeletes ? 0 : delete.deleteAllPermanent(beans);
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE_ALL_PERMANENT).with("beans", beans));
    capturedBeans.addDeletedAllPermanent(beans);
    return !persistDeletes ? 0 : delete.deleteAllPermanent(beans, transaction);
  }

  @Override
  public <T> int delete(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of(DELETE).with("query", query));
    return !persistDeletes ? 0 : delete.delete(query, transaction);
  }

  @Override
  public int delete(Class<?> beanType, Object id) {
    MethodCall deleteById = MethodCall.of(DELETE).with("beanType", beanType, "id", id);
    methodCalls.add(deleteById);
    capturedBeans.addDeleted(deleteById);
    return !persistDeletes ? 0 : delete.delete(beanType, id, null);
  }

  @Override
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    MethodCall deleteById = MethodCall.of(DELETE).with("beanType", beanType, "id", id, "transaction", transaction);
    methodCalls.add(deleteById);
    capturedBeans.addDeleted(deleteById);
    return !persistDeletes ? 0 : delete.delete(beanType, id, transaction);
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id) {
    MethodCall deleteById = MethodCall.of(DELETE_PERMANENT).with("beanType", beanType, "id", id);
    methodCalls.add(deleteById);
    capturedBeans.addDeleted(deleteById);
    return !persistDeletes ? 0 : delete.deletePermanent(beanType, id);
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id, Transaction transaction) {
    MethodCall deleteById = MethodCall.of(DELETE_PERMANENT).with("beanType", beanType, "id", id, "transaction", transaction);
    methodCalls.add(deleteById);
    capturedBeans.addDeleted(deleteById);
    return !persistDeletes ? 0 : delete.deletePermanent(beanType, id, transaction);
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL).with("beanType", beanType, "ids", ids);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      return delete.deleteAll(beanType, ids, null);
    }
    return 0;
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL).with("beanType", beanType, "ids", ids, "transaction", transaction);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      return delete.deleteAll(beanType, ids, transaction);
    }
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL_PERMANENT).with("beanType", beanType, "ids", ids);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      return delete.deleteAllPermanent(beanType, ids);
    }
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL_PERMANENT).with("beanType", beanType, "ids", ids, "transaction", transaction);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      return delete.deleteAllPermanent(beanType, ids, transaction);
    }
    return 0;
  }

  @Override
  public boolean delete(Object bean, Transaction transaction) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(DELETE).with("bean", bean, "transaction", transaction));
    capturedBeans.addDeleted(bean);
    if (persistDeletes) {
      return delete.delete(bean, transaction);
    }
    return true;
  }


  // -- publish and restore ---------------------------


  @Override
  public <T> T publish(Class<T> beanType, Object id, Transaction transaction) {
    methodCalls.add(MethodCall.of(PUBLISH).with("beanType", beanType).with("id", id));
    return !persistPublish ? null : publish.publish(beanType, id, transaction);
  }

  @Override
  public <T> T publish(Class<T> beanType, Object id) {
    methodCalls.add(MethodCall.of(PUBLISH).with("beanType", beanType).with("id", id));
    return !persistPublish ? null : publish.publish(beanType, id);
  }

  @Override
  public <T> List<T> publish(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of(PUBLISH).with("query", query));
    return !persistPublish ? null : publish.publish(query, transaction);
  }

  @Override
  public <T> List<T> publish(Query<T> query) {
    methodCalls.add(MethodCall.of(PUBLISH).with("query", query));
    return !persistPublish ? null : publish.publish(query);
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id, Transaction transaction) {
    methodCalls.add(MethodCall.of(DRAFT_RESTORE).with("beanType", beanType).with("id", id));
    return !persistPublish ? null : publish.draftRestore(beanType, id, transaction);
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id) {
    methodCalls.add(MethodCall.of(DRAFT_RESTORE).with("beanType", beanType).with("id", id));
    return !persistPublish ? null : publish.draftRestore(beanType, id);
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of(DRAFT_RESTORE).with("query", query));
    return !persistPublish ? null : publish.draftRestore(query, transaction);
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query) {
    methodCalls.add(MethodCall.of(DRAFT_RESTORE).with("query", query));
    return !persistPublish ? null : publish.draftRestore(query);
  }


  // -- bulkUpdate bulkUpdates ------------------------

  @Override
  public int executeNow(SpiSqlUpdate sqlUpdate) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("executeNow", sqlUpdate));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(sqlUpdate);
  }

  @Override
  public int execute(SqlUpdate sqlUpdate) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("sqlUpdate", sqlUpdate));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(sqlUpdate);
  }

  @Override
  public int execute(Update<?> update) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("update", update));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(update);
  }

  @Override
  public int execute(Update<?> update, Transaction transaction) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("update", update, "transaction", transaction));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(update, transaction);
  }

  @Override
  public int execute(CallableSql callableSql) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("callableSql", callableSql));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(callableSql);
  }

  @Override
  public int execute(SqlUpdate sqlUpdate, Transaction transaction) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("sqlUpdate", sqlUpdate, "transaction", transaction));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(sqlUpdate, transaction);
  }

  @Override
  public int execute(CallableSql callableSql, Transaction transaction) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("callableSql", callableSql, "transaction", transaction));
    return !persistBulkUpdates ? 0 : bulkUpdate.execute(callableSql, transaction);
  }

  @Override
  public void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted) {

    methodCalls.add(MethodCall.of("externalModification")
      .with("tableName", tableName)
      .with("inserted", inserted, "updated", updated, "deleted", deleted));

    if (persistBulkUpdates) {
      bulkUpdate.externalModification(tableName, inserted, updated, deleted);
    }
  }

  @Override
  public <T> T findSingleAttribute(SpiSqlQuery query, Class<T> cls) {
    return spiDelegate().findSingleAttribute(query, cls);
  }

  @Override
  public <T> List<T> findSingleAttributeList(SpiSqlQuery query, Class<T> cls) {
    return spiDelegate().findSingleAttributeList(query, cls);
  }

  @Override
  public <T> void findSingleAttributeEach(SpiSqlQuery spiSqlQuery, Class<T> aClass, Consumer<T> consumer) {
    spiDelegate().findSingleAttributeEach(spiSqlQuery, aClass, consumer);
  }

  @Override
  public <T> T findOneMapper(SpiSqlQuery query, RowMapper<T> mapper) {
    return spiDelegate().findOneMapper(query, mapper);
  }

  @Override
  public <T> List<T> findListMapper(SpiSqlQuery query, RowMapper<T> mapper) {
    return spiDelegate().findListMapper(query, mapper);
  }

  @Override
  public void findEachRow(SpiSqlQuery query, RowConsumer consumer) {
    spiDelegate().findEachRow(query, consumer);
  }

  @Override
  public <T> QueryIterator<T> findDtoIterate(SpiDtoQuery<T> spiDtoQuery) {
    return spiDelegate().findDtoIterate(spiDtoQuery);
  }

  @Override
  public <T> Stream<T> findDtoStream(SpiDtoQuery<T> spiDtoQuery) {
    return spiDelegate().findDtoStream(spiDtoQuery);
  }

  @Override
  public ExtendedServer extended() {
    return delegate.extended();
  }

  @Override
  public long clockNow() {
    return spiDelegate().clockNow();
  }

  @Override
  public void setClock(Clock clock) {
    spiDelegate().setClock(clock);
  }

  private SpiEbeanServer spiDelegate() {
    return (SpiEbeanServer) delegate;
  }

  @Override
  public SpiLogManager log() {
    return spiDelegate().log();
  }

  @Override
  public SpiJsonContext jsonExtended() {
    return spiDelegate().jsonExtended();
  }

  @Override
  public void shutdown() {
    spiDelegate().shutdown();
  }

  @Override
  public boolean isUpdateAllPropertiesInBatch() {
    return spiDelegate().isUpdateAllPropertiesInBatch();
  }

  @Override
  public Object currentTenantId() {
    return spiDelegate().isUpdateAllPropertiesInBatch();
  }

  @Override
  public DatabaseConfig config() {
    return spiDelegate().config();
  }

  @Override
  public DatabasePlatform databasePlatform() {
    return spiDelegate().databasePlatform();
  }

  @Override
  public List<? extends BeanType<?>> beanTypes() {
    return spiDelegate().beanTypes();
  }

  @Override
  public <T> BeanType<T> beanType(Class<T> aClass) {
    return spiDelegate().beanType(aClass);
  }

  @Override
  public List<? extends BeanType<?>> beanTypes(String s) {
    return spiDelegate().beanTypes(s);
  }

  @Override
  public BeanType<?> beanTypeForQueueId(String s) {
    return spiDelegate().beanTypeForQueueId(s);
  }

  @Override
  public BeanLoader beanLoader() {
    return spiDelegate().beanLoader();
  }

  @Override
  public void loadBeanRef(EntityBeanIntercept entityBeanIntercept) {
    spiDelegate().loadBeanRef(entityBeanIntercept);
  }

  @Override
  public void loadBeanL2(EntityBeanIntercept entityBeanIntercept) {
    spiDelegate().loadBeanL2(entityBeanIntercept);
  }

  @Override
  public void loadBean(EntityBeanIntercept entityBeanIntercept) {

  }

  @Override
  public CallOrigin createCallOrigin() {
    return spiDelegate().createCallOrigin();
  }

  @Override
  public PersistenceContextScope persistenceContextScope(SpiQuery<?> query) {
    return spiDelegate().persistenceContextScope(query);
  }

  @Override
  public void clearQueryStatistics() {
    spiDelegate().clearQueryStatistics();
  }

  @Override
  public SpiTransactionManager transactionManager() {
    return spiDelegate().transactionManager();
  }

  @Override
  public List<BeanDescriptor<?>> descriptors() {
    return spiDelegate().descriptors();
  }

  @Override
  public <T> BeanDescriptor<T> descriptor(Class<T> type) {
    return spiDelegate().descriptor(type);
  }

  @Override
  public BeanDescriptor<?> descriptorById(String className) {
    return spiDelegate().descriptorById(className);
  }

  @Override
  public BeanDescriptor<?> descriptorByQueueId(String queueId) {
    return spiDelegate().descriptorByQueueId(queueId);
  }

  @Override
  public List<BeanDescriptor<?>> descriptors(String tableName) {
    return spiDelegate().descriptors(tableName);
  }

  @Override
  public void externalModification(TransactionEventTable event) {
    spiDelegate().externalModification(event);
  }

  @Override
  public SpiTransaction beginServerTransaction() {
    return spiDelegate().beginServerTransaction();
  }

  @Override
  public SpiTransaction currentServerTransaction() {
    return spiDelegate().currentServerTransaction();
  }

  @Override
  public void remoteTransactionEvent(RemoteTransactionEvent event) {
    spiDelegate().remoteTransactionEvent(event);
  }

  @Override
  public <T> CQuery<T> compileQuery(SpiQuery.Type type, Query<T> query, Transaction transaction) {
    return spiDelegate().compileQuery(type, query, transaction);
  }

  @Override
  public <A, T> List<A> findIdsWithCopy(Query<T> query, Transaction t) {
    return spiDelegate().findIdsWithCopy(query, t);
  }

  @Override
  public <T> int findCountWithCopy(Query<T> query, Transaction t) {
    return spiDelegate().findCountWithCopy(query, t);
  }

  @Override
  public void loadBean(LoadBeanRequest loadRequest) {
    spiDelegate().loadBean(loadRequest);
  }

  @Override
  public void loadMany(LoadManyRequest loadRequest) {
    spiDelegate().loadMany(loadRequest);
  }

  @Override
  public int lazyLoadBatchSize() {
    return spiDelegate().lazyLoadBatchSize();
  }

  @Override
  public boolean isSupportedType(Type genericType) {
    return spiDelegate().isSupportedType(genericType);
  }

  @Override
  public ReadAuditLogger readAuditLogger() {
    return spiDelegate().readAuditLogger();
  }

  @Override
  public ReadAuditPrepare readAuditPrepare() {
    return spiDelegate().readAuditPrepare();
  }

  @Override
  public DataTimeZone dataTimeZone() {
    return spiDelegate().dataTimeZone();
  }

  @Override
  public void slowQueryCheck(long executionTimeMicros, int rowCount, SpiQuery<?> query) {
    spiDelegate().slowQueryCheck(executionTimeMicros, rowCount, query);
  }

  @Override
  public void scopedTransactionEnter(TxScope txScope) {
    spiDelegate().scopedTransactionEnter(txScope);
  }

  @Override
  public void scopedTransactionExit(Object returnOrThrowable, int opCode) {
    spiDelegate().scopedTransactionExit(returnOrThrowable, opCode);
  }

  @Override
  public <T> List<T> findDtoList(SpiDtoQuery<T> query) {
    return spiDelegate().findDtoList(query);
  }

  @Override
  public <T> T findDtoOne(SpiDtoQuery<T> query) {
    return spiDelegate().findDtoOne(query);
  }

  @Override
  public <T> void findDtoEach(SpiDtoQuery<T> query, Consumer<T> consumer) {
    spiDelegate().findDtoEach(query, consumer);
  }

  @Override
  public <T> void findDtoEach(SpiDtoQuery<T> spiDtoQuery, int i, Consumer<List<T>> consumer) {
    spiDelegate().findDtoEach(spiDtoQuery, i, consumer);
  }

  @Override
  public <T> void findDtoEachWhile(SpiDtoQuery<T> query, Predicate<T> consumer) {
    spiDelegate().findDtoEachWhile(query, consumer);
  }

  @Override
  public <D> DtoQuery<D> findDto(Class<D> dtoType, SpiQuery<?> ormQuery) {
    return spiDelegate().findDto(dtoType, ormQuery);
  }

  @Override
  public SpiResultSet findResultSet(SpiQuery<?> ormQuery, SpiTransaction transaction) {
    return spiDelegate().findResultSet(ormQuery, transaction);
  }

  @Override
  public void visitMetrics(MetricVisitor visitor) {
    spiDelegate().visitMetrics(visitor);
  }

  @Override
  public boolean exists(Class<?> beanType, Object beanId, Transaction transaction) {
    return spiDelegate().exists(beanType, beanId, transaction);
  }

  @Override
  public void addBatch(SpiSqlUpdate sqlUpdate, SpiTransaction transaction) {
    spiDelegate().addBatch(sqlUpdate, transaction);
  }

  @Override
  public int[] executeBatch(SpiSqlUpdate sqlUpdate, SpiTransaction transaction) {
    return spiDelegate().executeBatch(sqlUpdate, transaction);
  }

  @Override
  public void loadMany(BeanCollection<?> collection, boolean onlyIds) {
    spiDelegate().loadMany(collection, onlyIds);
  }

}
