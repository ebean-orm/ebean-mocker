package com.avaje.ebean;

import com.avaje.ebean.cache.ServerCacheManager;
import com.avaje.ebean.meta.MetaInfoManager;
import com.avaje.ebean.text.csv.CsvReader;
import com.avaje.ebean.text.json.JsonContext;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps an underlying EbeanServer.
 *
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class WrappingEbeanServer implements EbeanServer {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delete to.
   *
   * This delegate will be used on all method calls that are not overwritten.
   */
  public WrappingEbeanServer(EbeanServer delegate) {
    this.delegate = delegate;
  }

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {
    delegate.shutdown(shutdownDataSource, deregisterDriver);
  }

  @Override
  public AdminAutofetch getAdminAutofetch() {
    return delegate.getAdminAutofetch();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public ExpressionFactory getExpressionFactory() {
    return delegate.getExpressionFactory();
  }

  @Override
  public MetaInfoManager getMetaInfoManager() {
    return delegate.getMetaInfoManager();
  }

  @Override
  public BeanState getBeanState(Object bean) {
    return delegate.getBeanState(bean);
  }

  @Override
  public Object getBeanId(Object bean) {
    return delegate.getBeanId(bean);
  }

  @Override
  public Map<String, ValuePair> diff(Object a, Object b) {
    return delegate.diff(a, b);
  }

  @Override
  public <T> T createEntityBean(Class<T> type) {
    return delegate.createEntityBean(type);
  }

  @Override
  public <T> CsvReader<T> createCsvReader(Class<T> beanType) {
    return delegate.createCsvReader(beanType);
  }

  @Override
  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    return delegate.createNamedQuery(beanType, namedQuery);
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType, String query) {
    return delegate.createQuery(beanType, query);
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType) {
    return delegate.createQuery(beanType);
  }

  @Override
  public <T> Query<T> find(Class<T> beanType) {
    return delegate.find(beanType);
  }

  @Override
  public Object nextId(Class<?> beanType) {
    return delegate.nextId(beanType);
  }

  @Override
  public <T> Filter<T> filter(Class<T> beanType) {
    return delegate.filter(beanType);
  }

  @Override
  public <T> void sort(List<T> list, String sortByClause) {
    delegate.sort(list, sortByClause);
  }

  @Override
  public <T> Update<T> createNamedUpdate(Class<T> beanType, String namedUpdate) {
    return delegate.createNamedUpdate(beanType, namedUpdate);
  }

  @Override
  public <T> Update<T> createUpdate(Class<T> beanType, String ormUpdate) {
    return delegate.createUpdate(beanType, ormUpdate);
  }

  @Override
  public SqlQuery createSqlQuery(String sql) {
    return delegate.createSqlQuery(sql);
  }

  @Override
  public SqlQuery createNamedSqlQuery(String namedQuery) {
    return delegate.createNamedSqlQuery(namedQuery);
  }

  @Override
  public SqlUpdate createSqlUpdate(String sql) {
    return delegate.createSqlUpdate(sql);
  }

  @Override
  public CallableSql createCallableSql(String callableSql) {
    return delegate.createCallableSql(callableSql);
  }

  @Override
  public SqlUpdate createNamedSqlUpdate(String namedQuery) {
    return delegate.createNamedSqlUpdate(namedQuery);
  }

  @Override
  public void register(TransactionCallback transactionCallback) throws PersistenceException {
    delegate.register(transactionCallback);
  }

  @Override
  public Transaction createTransaction() {
    return delegate.createTransaction();
  }

  @Override
  public Transaction createTransaction(TxIsolation isolation) {
    return delegate.createTransaction(isolation);
  }

  @Override
  public Transaction beginTransaction() {
    return delegate.beginTransaction();
  }

  @Override
  public Transaction beginTransaction(TxIsolation isolation) {
    return delegate.beginTransaction(isolation);
  }

  @Override
  public Transaction beginTransaction(TxScope scope) {
    return delegate.beginTransaction(scope);
  }

  @Override
  public Transaction currentTransaction() {
    return delegate.currentTransaction();
  }

  @Override
  public void commitTransaction() {
    delegate.commitTransaction();
  }

  @Override
  public void rollbackTransaction() {
    delegate.rollbackTransaction();
  }

  @Override
  public void endTransaction() {
    delegate.endTransaction();
  }

  @Override
  public void refresh(Object bean) {
    delegate.refresh(bean);
  }

  @Override
  public void refreshMany(Object bean, String propertyName) {
    delegate.refreshMany(bean, propertyName);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id) {
    return delegate.find(beanType, id);
  }

  @Override
  public <T> T getReference(Class<T> beanType, Object id) {
    return delegate.getReference(beanType, id);
  }

  @Override
  public <T> int findRowCount(Query<T> query, Transaction transaction) {
    return delegate.findRowCount(query, transaction);
  }

  @Override
  public <T> List<Object> findIds(Query<T> query, Transaction transaction) {
    return delegate.findIds(query, transaction);
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    return delegate.findIterate(query, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction) {
    delegate.findEach(query, consumer, transaction);
  }

  @Override
  public <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction) {
    delegate.findEachWhile(query, consumer, transaction);
  }

  @Override
  public <T> void findVisit(Query<T> query, QueryResultVisitor<T> visitor, Transaction transaction) {
    delegate.findVisit(query, visitor, transaction);
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    return delegate.findList(query, transaction);
  }

  @Override
  public <T> FutureRowCount<T> findFutureRowCount(Query<T> query, Transaction transaction) {
    return delegate.findFutureRowCount(query, transaction);
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    return delegate.findFutureIds(query, transaction);
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    return delegate.findFutureList(query, transaction);
  }

  @Override
  public SqlFutureList findFutureList(SqlQuery query, Transaction transaction) {
    return delegate.findFutureList(query, transaction);
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction, int pageIndex, int pageSize) {
    return delegate.findPagedList(query, transaction, pageIndex, pageSize);
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    return delegate.findSet(query, transaction);
  }

  @Override
  public <T> Map<?, T> findMap(Query<T> query, Transaction transaction) {
    return delegate.findMap(query, transaction);
  }

  @Override
  public <T> T findUnique(Query<T> query, Transaction transaction) {
    return delegate.findUnique(query, transaction);
  }

  @Override
  public List<SqlRow> findList(SqlQuery query, Transaction transaction) {
    return delegate.findList(query, transaction);
  }

  @Override
  public Set<SqlRow> findSet(SqlQuery query, Transaction transaction) {
    return delegate.findSet(query, transaction);
  }

  @Override
  public Map<?, SqlRow> findMap(SqlQuery query, Transaction transaction) {
    return delegate.findMap(query, transaction);
  }

  @Override
  public SqlRow findUnique(SqlQuery query, Transaction transaction) {
    return delegate.findUnique(query, transaction);
  }

  @Override
  public void save(Object bean) throws OptimisticLockException {
    delegate.save(bean);
  }

  @Override
  public int save(Iterator<?> it) throws OptimisticLockException {
    return delegate.save(it);
  }

  @Override
  public int save(Collection<?> beans) throws OptimisticLockException {
    return delegate.save(beans);
  }

  @Override
  public void delete(Object bean) throws OptimisticLockException {
    delegate.delete(bean);
  }

  @Override
  public int delete(Iterator<?> it) throws OptimisticLockException {
    return delegate.delete(it);
  }

  @Override
  public int delete(Collection<?> c) throws OptimisticLockException {
    return delegate.delete(c);
  }

  @Override
  public int delete(Class<?> beanType, Object id) {
    return delegate.delete(beanType, id);
  }

  @Override
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    return delegate.delete(beanType, id, transaction);
  }

  @Override
  public void delete(Class<?> beanType, Collection<?> ids) {
    delegate.delete(beanType, ids);
  }

  @Override
  public void delete(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    delegate.delete(beanType, ids, transaction);
  }

  @Override
  public int execute(SqlUpdate sqlUpdate) {
    return delegate.execute(sqlUpdate);
  }

  @Override
  public int execute(Update<?> update) {
    return delegate.execute(update);
  }

  @Override
  public int execute(Update<?> update, Transaction t) {
    return delegate.execute(update, t);
  }

  @Override
  public int execute(CallableSql callableSql) {
    return delegate.execute(callableSql);
  }

  @Override
  public void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted) {
    delegate.externalModification(tableName, inserted, updated, deleted);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id, Transaction transaction) {
    return delegate.find(beanType, id, transaction);
  }

  @Override
  public void save(Object bean, Transaction transaction) throws OptimisticLockException {
    delegate.save(bean, transaction);
  }

  @Override
  public int save(Iterator<?> it, Transaction transaction) throws OptimisticLockException {
    return delegate.save(it, transaction);
  }

  @Override
  public int save(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    return delegate.save(beans, transaction);
  }

  @Override
  public void markAsDirty(Object bean) {
    delegate.markAsDirty(bean);
  }

  @Override
  public void update(Object bean) throws OptimisticLockException {
    delegate.update(bean);
  }

  @Override
  public void update(Object bean, Transaction t) throws OptimisticLockException {
    delegate.update(bean, t);
  }

  @Override
  public void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException {
    delegate.update(bean, transaction, deleteMissingChildren);
  }

  @Override
  public void update(Collection<?> beans) throws OptimisticLockException {
    delegate.update(beans);
  }

  @Override
  public void update(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    delegate.update(beans, transaction);
  }

  @Override
  public void insert(Object bean) {
    delegate.insert(bean);
  }

  @Override
  public void insert(Object bean, Transaction t) {
    delegate.insert(bean, t);
  }

  @Override
  public void insert(Collection<?> beans) {
    delegate.insert(beans);
  }

  @Override
  public void insert(Collection<?> beans, Transaction t) {
    delegate.insert(beans, t);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName, t);
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName) {
    delegate.saveManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    delegate.saveManyToManyAssociations(ownerBean, propertyName, t);
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName) {
    delegate.saveAssociation(ownerBean, propertyName);
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName, Transaction t) {
    delegate.saveAssociation(ownerBean, propertyName, t);
  }

  @Override
  public void delete(Object bean, Transaction t) throws OptimisticLockException {
    delegate.delete(bean, t);
  }

  @Override
  public int delete(Iterator<?> it, Transaction t) throws OptimisticLockException {
    return delegate.delete(it, t);
  }

  @Override
  public int execute(SqlUpdate updSql, Transaction t) {
    return delegate.execute(updSql, t);
  }

  @Override
  public int execute(CallableSql callableSql, Transaction t) {
    return delegate.execute(callableSql, t);
  }

  @Override
  public void execute(TxScope scope, TxRunnable r) {
    delegate.execute(scope, r);
  }

  @Override
  public void execute(TxRunnable r) {
    delegate.execute(r);
  }

  @Override
  public <T> T execute(TxScope scope, TxCallable<T> c) {
    return delegate.execute(scope, c);
  }

  @Override
  public <T> T execute(TxCallable<T> c) {
    return delegate.execute(c);
  }

  @Override
  public ServerCacheManager getServerCacheManager() {
    return delegate.getServerCacheManager();
  }

  @Override
  public BackgroundExecutor getBackgroundExecutor() {
    return delegate.getBackgroundExecutor();
  }

  @Override
  public void runCacheWarming() {
    delegate.runCacheWarming();
  }

  @Override
  public void runCacheWarming(Class<?> beanType) {
    delegate.runCacheWarming(beanType);
  }

  @Override
  public JsonContext createJsonContext() {
    return delegate.createJsonContext();
  }

  @Override
  public JsonContext json() {
    return delegate.json();
  }
}
