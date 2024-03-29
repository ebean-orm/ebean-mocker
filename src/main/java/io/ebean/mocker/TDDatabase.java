package io.ebean.mocker;

import io.ebean.*;
import io.ebean.annotation.Platform;
import io.ebean.annotation.TxIsolation;
import io.ebean.cache.ServerCacheManager;
import io.ebean.meta.MetaInfoManager;
import io.ebean.plugin.Property;
import io.ebean.plugin.SpiServer;
import io.ebean.text.csv.CsvReader;
import io.ebean.text.json.JsonContext;

import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Test double for Database.
 */
public class TDDatabase implements Database {

  public List<Object> deletedBeans = new ArrayList<>();

  public List<Object> savedBeans = new ArrayList<>();

  @Override
  public void shutdown(boolean shutdownDataSource, boolean deregisterDriver) {

  }

  @Override
  public ExtendedServer extended() {
    return null;
  }

  @Override
  public DocumentStore docStore() {
    return null;
  }

  @Override
  public SpiServer pluginApi() {
    return null;
  }

  @Override
  public AutoTune autoTune() {
    return null;
  }

  @Override
  public String name() {
    return null;
  }

  @Override
  public ExpressionFactory expressionFactory() {
    return null;
  }

  @Override
  public MetaInfoManager metaInfo() {
    return null;
  }

  @Override
  public BeanState beanState(Object bean) {
    return null;
  }

  @Override
  public Object beanId(Object bean) {
    return null;
  }

  @Override
  public Object beanId(Object bean, Object id) {
    return id;
  }

  @Override
  public Map<String, ValuePair> diff(Object a, Object b) {
    return null;
  }

  @Override
  public <T> T createEntityBean(Class<T> type) {
    return null;
  }

  @Override
  public <T> CsvReader<T> createCsvReader(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    return null;
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType, String eql) {
    return null;
  }

  @Override
  public <T> Query<T> createQuery(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> Query<T> find(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> Query<T> findNative(Class<T> beanType, String nativeSql) {
    return null;
  }

  @Override
  public Object nextId(Class<?> beanType) {
    return null;
  }

  @Override
  public <T> Filter<T> filter(Class<T> beanType) {
    return null;
  }

  @Override
  public <T> void sort(List<T> list, String sortByClause) {

  }

  @Override
  public <T> Update<T> createUpdate(Class<T> beanType, String ormUpdate) {
    return null;
  }

  @Override
  public CallableSql createCallableSql(String callableSql) {
    return null;
  }

  @Override
  public void register(TransactionCallback transactionCallback) throws PersistenceException {

  }

  @Override
  public Transaction createTransaction() {
    return null;
  }

  @Override
  public Transaction createTransaction(TxIsolation isolation) {
    return null;
  }

  @Override
  public Transaction beginTransaction() {
    return null;
  }

  @Override
  public Transaction beginTransaction(TxIsolation isolation) {
    return null;
  }

  @Override
  public Transaction beginTransaction(TxScope scope) {
    return null;
  }

  @Override
  public Transaction currentTransaction() {
    return null;
  }

  @Override
  public void flush() {

  }

  @Override
  public void commitTransaction() {

  }

  @Override
  public void rollbackTransaction() {

  }

  @Override
  public void endTransaction() {

  }

  @Override
  public void refresh(Object bean) {

  }

  @Override
  public void refreshMany(Object bean, String propertyName) {

  }

  @Override
  public <T> T find(Class<T> beanType, Object id) {
    return null;
  }

  @Override
  public <T> T reference(Class<T> beanType, Object id) {
    return null;
  }


  @Override
  public <T> Set<String> validateQuery(Query<T> query) {
    return null;
  }

  @Override
  public void lock(Object o) {

  }

  @Override
  public <T> DtoQuery<T> findDto(Class<T> dtoType, String sql) {
    return null;
  }

  @Override
  public <T> DtoQuery<T> createNamedDtoQuery(Class<T> dtoType, String namedQuery) {
    return null;
  }

  @Override
  public Set<Property> checkUniqueness(Object bean) {
    return null;
  }

  @Override
  public Set<Property> checkUniqueness(Object bean, Transaction transaction) {
    return null;
  }

  @Override
  public void merge(Object bean) {

  }

  @Override
  public void merge(Object bean, MergeOptions options) {

  }

  @Override
  public void merge(Object bean, MergeOptions options, Transaction transaction) {

  }

  @Override
  public void save(Object bean) throws OptimisticLockException {
    savedBeans.add(bean);
  }

  @Override
  public int saveAll(Collection<?> beans) throws OptimisticLockException {
    savedBeans.addAll(beans);
    return 0;
  }

  @Override
  public boolean delete(Object bean) throws OptimisticLockException {
    deletedBeans.add(bean);
    return false;
  }

  @Override
  public int deleteAll(Collection<?> c) throws OptimisticLockException {
    deletedBeans.addAll(c);
    return 0;
  }

  @Override
  public int deleteAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    return 0;
  }

  @Override
  public int delete(Class<?> beanType, Object id) {
    return 0;
  }

  @Override
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    return 0;
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id) {
    return 0;
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id, Transaction transaction) {
    return 0;
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids) {
    return 0;
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    return 0;
  }

  @Override
  public boolean deletePermanent(Object bean) throws OptimisticLockException {
    return false;
  }

  @Override
  public boolean deletePermanent(Object bean, Transaction transaction) throws OptimisticLockException {
    return false;
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans) throws OptimisticLockException {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids) {
    return 0;
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    return 0;
  }

  @Override
  public int execute(SqlUpdate sqlUpdate) {
    return 0;
  }

  @Override
  public int execute(Update<?> update) {
    return 0;
  }

  @Override
  public int execute(Update<?> update, Transaction t) {
    return 0;
  }

  @Override
  public int execute(CallableSql callableSql) {
    return 0;
  }

  @Override
  public void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted) {

  }

  @Override
  public <T> T publish(Class<T> beanType, Object id, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T publish(Class<T> beanType, Object id) {
    return null;
  }

  @Override
  public <T> List<T> publish(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> List<T> publish(Query<T> query) {
    return null;
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id, Transaction transaction) {
    return null;
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id) {
    return null;
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query, Transaction transaction) {
    return null;
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query) {
    return null;
  }

  @Override
  public <T> T find(Class<T> beanType, Object uid, Transaction transaction) {
    return null;
  }

  @Override
  public void save(Object bean, Transaction transaction) throws OptimisticLockException {
    savedBeans.add(bean);
  }

  @Override
  public int saveAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    savedBeans.addAll(beans);
    return 0;
  }

  @Override
  public void markAsDirty(Object bean) {

  }

  @Override
  public <T> UpdateQuery<T> update(Class<T> beanType) {
    return null;
  }

  @Override
  public void update(Object bean) throws OptimisticLockException {

  }

  @Override
  public void update(Object bean, Transaction t) throws OptimisticLockException {

  }

  @Override
  public void updateAll(Collection<?> beans) throws OptimisticLockException {

  }

  @Override
  public void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {

  }

  @Override
  public void insert(Object bean) {

  }

  @Override
  public void insert(Object bean, Transaction t) {

  }

  @Override
  public void insertAll(Collection<?> beans) {

  }

  @Override
  public void insertAll(Collection<?> beans, Transaction t) {

  }

  @Override
  public boolean delete(Object bean, Transaction t) throws OptimisticLockException {
    return false;
  }

  @Override
  public int execute(SqlUpdate updSql, Transaction t) {
    return 0;
  }

  @Override
  public int execute(CallableSql callableSql, Transaction t) {
    return 0;
  }

  @Override
  public void execute(TxScope scope, Runnable r) {

  }

  @Override
  public void execute(Runnable r) {

  }

  @Override
  public <T> T executeCall(TxScope scope, Callable<T> c) {
    return null;
  }

  @Override
  public <T> T executeCall(Callable<T> c) {
    return null;
  }

  @Override
  public ServerCacheManager cacheManager() {
    return null;
  }

  @Override
  public BackgroundExecutor backgroundExecutor() {
    return null;
  }

  @Override
  public JsonContext json() {
    return null;
  }

  @Override
  public void shutdown() {

  }

  @Override
  public Platform platform() {
    return null;
  }

  @Override
  public SqlQuery sqlQuery(String sql) {
    return null;
  }

  @Override
  public SqlUpdate sqlUpdate(String sql) {
    return null;
  }

  @Override
  public int saveAll(Object... beans) throws OptimisticLockException {
    return 0;
  }

  @Override
  public ScriptRunner script() {
    return null;
  }

  @Override
  public void truncate(String... tables) {

  }

  @Override
  public void truncate(Class<?>... tables) {

  }

  @Override
  public DataSource dataSource() {
    return null;
  }

  @Override
  public DataSource readOnlyDataSource() {
    return null;
  }
}
