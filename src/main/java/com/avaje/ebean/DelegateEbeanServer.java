package com.avaje.ebean;

import com.avaje.ebean.backgroundexecutor.ImmediateBackgroundExecutor;
import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.ebean.delegate.DelegateBulkUpdate;
import com.avaje.ebean.delegate.DelegateDelete;
import com.avaje.ebean.delegate.DelegateFind;
import com.avaje.ebean.delegate.DelegateFindSqlQuery;
import com.avaje.ebean.delegate.DelegatePublish;
import com.avaje.ebean.delegate.DelegateQuery;
import com.avaje.ebean.delegate.DelegateSave;
import com.avaje.ebean.delegate.InterceptBulkUpdate;
import com.avaje.ebean.delegate.InterceptDelete;
import com.avaje.ebean.delegate.InterceptFind;
import com.avaje.ebean.delegate.InterceptFindSqlQuery;
import com.avaje.ebean.delegate.InterceptPublish;
import com.avaje.ebean.delegate.InterceptSave;
import com.avaje.ebean.meta.MetaInfoManager;
import com.avaje.ebean.plugin.SpiServer;
import com.avaje.ebean.text.csv.CsvReader;
import com.avaje.ebean.text.json.JsonContext;
import com.avaje.ebeaninternal.api.SpiQuery;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DelegateEbeanServer implements EbeanServer, DelegateAwareEbeanServer, DelegateMethodNames {

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
  protected EbeanServer delegate;

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
  public DelegateEbeanServer withDelegate(EbeanServer delegate) {
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
  public boolean withDelegateIfRequired(EbeanServer delegate) {
    if (this.delegate == null) {
      withDelegate(delegate);
      return true;
    }
    if (this.delegate instanceof DelegateAwareEbeanServer) {
      // probably using ProxyEbeanServer to capture method calls etc
      return ((DelegateAwareEbeanServer)this.delegate).withDelegateIfRequired(delegate);
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
  public SpiServer getPluginApi() {
    methodCalls.add(MethodCall.of("getPluginApi"));
    return delegate.getPluginApi();
  }

  @Override
  public AutoTune getAutoTune() {
    methodCalls.add(MethodCall.of("getAutoTune"));
    return delegate.getAutoTune();
  }

  @Override
  public DocumentStore docStore() {
    methodCalls.add(MethodCall.of("docStore"));
    return delegate.docStore();
  }

  /**
   * Return the BackgroundExecutor.
   *
   * Typically for testing we either want these to run immediately or not at all.
   * Defaults to use ImmediateBackgroundExecutor, use IgnoreBackgroundExecutor if desired.
   */
  @Override
  public BackgroundExecutor getBackgroundExecutor() {
    methodCalls.add(MethodCall.of("getBackgroundExecutor"));
    return backgroundExecutor;
  }

  @Override
  public ServerCacheManager getServerCacheManager() {
    methodCalls.add(MethodCall.of("getServerCacheManager"));
    return delegate.getServerCacheManager();
  }

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {
    methodCalls.add(MethodCall.of("shutdown").with("shutdownDataSource", shutdownDataSource, "deregisterDriver", deregisterDriver));
    delegate.shutdown(shutdownDataSource, deregisterDriver);
  }

  @Override
  public void runCacheWarming() {
    methodCalls.add(MethodCall.of("runCacheWarming"));
    delegate.runCacheWarming();
  }

  @Override
  public void runCacheWarming(Class<?> beanType) {
    methodCalls.add(MethodCall.of("runCacheWarming").with("beanType", beanType));
    delegate.runCacheWarming(beanType);
  }

  @Override
  public JsonContext json() {
    methodCalls.add(MethodCall.of("json"));
    return delegate.json();
  }

  @Override
  public String getName() {
    methodCalls.add(MethodCall.of("getName"));
    return delegate.getName();
  }

  @Override
  public ExpressionFactory getExpressionFactory() {
    methodCalls.add(MethodCall.of("getExpressionFactory"));
    return delegate.getExpressionFactory();
  }

  @Override
  public MetaInfoManager getMetaInfoManager() {
    methodCalls.add(MethodCall.of("getMetaInfoManager"));
    return delegate.getMetaInfoManager();
  }

  @Override
  public BeanState getBeanState(Object bean) {
    methodCalls.add(MethodCall.of("getBeanState").with("bean", bean));
    return delegate.getBeanState(bean);
  }

  @Override
  public Object getBeanId(Object bean) {
    methodCalls.add(MethodCall.of("getBeanId").with("bean", bean));
    return delegate.getBeanId(bean);
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
  public <T> Update<T> createNamedUpdate(Class<T> beanType, String namedUpdate) {
    methodCalls.add(MethodCall.of("createNamedUpdate").with("beanType", beanType, "namedUpdate", namedUpdate));
    return delegate.createNamedUpdate(beanType, namedUpdate);
  }

  @Override
  public <T> Update<T> createUpdate(Class<T> beanType, String ormUpdate) {
    methodCalls.add(MethodCall.of("createUpdate").with("beanType", beanType, "ormUpdate", ormUpdate));
    return delegate.createUpdate(beanType, ormUpdate);
  }

  @Override
  public SqlUpdate createSqlUpdate(String sql) {
    methodCalls.add(MethodCall.of("createSqlUpdate").with("sql", sql));
    return delegate.createSqlUpdate(sql);
  }

  @Override
  public CallableSql createCallableSql(String callableSql) {
    methodCalls.add(MethodCall.of("createCallableSql").with("callableSql", callableSql));
    return delegate.createCallableSql(callableSql);
  }

  @Override
  public SqlUpdate createNamedSqlUpdate(String namedQuery) {
    methodCalls.add(MethodCall.of("createNamedSqlUpdate").with("namedQuery", namedQuery));
    return delegate.createNamedSqlUpdate(namedQuery);
  }


  // -- transaction ------------------------

  @Override
  public void execute(TxScope scope, TxRunnable runnable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("scope", scope, "runnable", runnable));
    delegate.execute(scope, runnable);
  }

  @Override
  public void execute(TxRunnable runnable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("runnable", runnable));
    delegate.execute(runnable);
  }

  @Override
  public <T> T execute(TxScope scope, TxCallable<T> callable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("scope", scope, "callable", callable));
    return delegate.execute(scope, callable);
  }

  @Override
  public <T> T execute(TxCallable<T> callable) {
    methodCalls.add(MethodCall.of("bulkUpdate").with("callable", callable));
    return delegate.execute(callable);
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
  public <T> T getReference(Class<T> beanType, Object id) {
    methodCalls.add(MethodCall.of("getReference").with("beanType", beanType, "id", id));
    return delegateQuery.getReference(beanType, id);
  }

  @Override
  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    methodCalls.add(MethodCall.of("createNamedQuery").with("beanType", beanType, "namedQuery", namedQuery));
    return delegateQuery.createNamedQuery(beanType, namedQuery);
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType, String query) {
    methodCalls.add(MethodCall.of("createQuery").with("beanType", beanType, "query", query));
    return delegateQuery.createQuery(beanType, query);
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
  public <T> Query<T> find(Class<T> beanType) {
    methodCalls.add(MethodCall.of("find").with("beanType", beanType));
    return delegateQuery.find(beanType);
  }

  @Override
  public SqlQuery createSqlQuery(String sql) {
    methodCalls.add(MethodCall.of("createSqlQuery").with("sql", sql));
    return delegateQuery.createSqlQuery(sql);
  }

  @Override
  public SqlQuery createNamedSqlQuery(String namedQuery) {
    methodCalls.add(MethodCall.of("createNamedSqlQuery").with("namedQuery", namedQuery));
    return delegateQuery.createNamedSqlQuery(namedQuery);
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
      return (T)match.val();
    }
    return find.find(beanType, id, null);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id, Transaction transaction) {
    methodCalls.add(MethodCall.of("find").with("beanType", beanType, "id", id, "transaction", transaction));
    WhenBeanReturn match = whenFind.findMatchById(beanType, id);
    if (match != null) {
      return (T)match.val();
    }
    return find.find(beanType, id, transaction);
  }

  @Override
  public <T> T findUnique(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findUnique").with("query", query, "transaction", transaction));
    WhenBeanReturn match = whenFind.findMatchByUnique(((SpiQuery)query).getBeanType());
    if (match != null) {
      return (T)match.val();
    }
    return find.findUnique(query, transaction);
  }

  @Override
  public <T> int findRowCount(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findRowCount").with("query", query, "transaction", transaction));
    return find.findRowCount(query, transaction);
  }

  @Override
  public <T> List<Object> findIds(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findIds").with("query", query, "transaction", transaction));
    return find.findIds(query, transaction);
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findIterate").with("query", query, "transaction", transaction));
    return find.findIterate(query, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEach").with("query", query, "consumer", consumer, "transaction", transaction));
    find.findEach(query, consumer, transaction);
  }

  @Override
  public <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction) {
    methodCalls.add(MethodCall.of("findEachWhile").with("query", query, "consumer", consumer, "transaction", transaction));
    find.findEachWhile(query, consumer, transaction);
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findList").with("query", query, "transaction", transaction));
    return find.findList(query, transaction);
  }

  @Override
  public <T> FutureRowCount<T> findFutureRowCount(Query<T> query, Transaction transaction) {
    methodCalls.add(MethodCall.of("findFutureRowCount").with("query", query, "transaction", transaction));
    return find.findFutureRowCount(query, transaction);
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
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction, int pageIndex, int pageSize) {
    methodCalls.add(MethodCall.of("findPagedList").with("query", query, "transaction", transaction).with("pageIndex", pageIndex, "pageSize", pageSize));
    return find.findPagedList(query, transaction, pageIndex, pageSize);
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
  public <T> Map<?, T> findMap(Query<T> query, Transaction transaction) {
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
  public SqlFutureList findFutureList(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findFutureList").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findFutureList(sqlQuery, transaction);
  }

  @Override
  public List<SqlRow> findList(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findList").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findList(sqlQuery, transaction);
  }

  @Override
  public Set<SqlRow> findSet(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findSet").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findSet(sqlQuery, transaction);
  }

  @Override
  public Map<?, SqlRow> findMap(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findMap").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findMap(sqlQuery, transaction);
  }

  @Override
  public SqlRow findUnique(SqlQuery sqlQuery, Transaction transaction) {
    methodCalls.add(MethodCall.of("findUnique").with("sqlQuery", sqlQuery, "transaction", transaction));
    return findSqlQuery.findUnique(sqlQuery, transaction);
  }


  // -- save ------------------------

  @Override
  public Object nextId(Class<?> beanType) {
    methodCalls.add(MethodCall.of("nextId").with("beanType", beanType));
    return !persistSaves ? 0 : save.nextId(beanType);
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
  public void saveManyToManyAssociations(Object ownerBean, String propertyName) {
    methodCalls.add(MethodCall.of("saveManyToManyAssociations").with("ownerBean", ownerBean, "propertyName", propertyName));
    if (persistSaves) {
      save.saveManyToManyAssociations(ownerBean, propertyName);
    }
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction transaction) {
    methodCalls.add(MethodCall.of("saveManyToManyAssociations").with("ownerBean", ownerBean, "propertyName", propertyName, "transaction", transaction));
    if (persistSaves) {
      save.saveManyToManyAssociations(ownerBean, propertyName, transaction);
    }
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName) {
    methodCalls.add(MethodCall.of("saveAssociation").with("ownerBean", ownerBean, "propertyName", propertyName));
    if (persistSaves) {
      save.saveAssociation(ownerBean, propertyName);
    }
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName, Transaction transaction) {
    methodCalls.add(MethodCall.of("saveAssociation").with("ownerBean", ownerBean, "propertyName", propertyName, "transaction", transaction));
    if (persistSaves) {
      save.saveAssociation(ownerBean, propertyName, transaction);
    }
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

  @Override
  public void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException {
    methodCalls.add(MethodCall.of(UPDATE).with("bean", bean, "transaction", transaction, "deleteMissingChildren", deleteMissingChildren));
    capturedBeans.addUpdated(bean);
    if (persistUpdates) {
      save.update(bean, transaction, deleteMissingChildren);
    }
  }

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
  public void deleteAll(Class<?> beanType, Collection<?> ids) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL).with("beanType", beanType, "ids", ids);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      delete.deleteAll(beanType, ids, null);
    }
  }

  @Override
  public void deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    MethodCall deleteByIds = MethodCall.of(DELETE_ALL).with("beanType", beanType, "ids", ids, "transaction", transaction);
    methodCalls.add(deleteByIds);
    capturedBeans.addDeleted(deleteByIds);
    if (persistDeletes) {
      delete.deleteAll(beanType, ids, transaction);
    }
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

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName) {
    methodCalls.add(MethodCall.of("deleteManyToManyAssociations").with("ownerBean", ownerBean, "propertyName", propertyName));
    return !persistDeletes ? 0 : delete.deleteManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction transaction) {
    methodCalls.add(MethodCall.of("deleteManyToManyAssociations").with("ownerBean", ownerBean, "propertyName", propertyName, "transaction", transaction));
    return !persistDeletes ? 0 : delete.deleteManyToManyAssociations(ownerBean, propertyName, transaction);
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

}
