package io.ebean;

import io.ebean.annotation.TxIsolation;
import org.example.domain.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class NoopEbeanServerTest {

  private io.ebean.NoopEbeanServer server = new io.ebean.NoopEbeanServer();

  @Test
  public void testCreateEntityBean() throws Exception {

    //Mockito.mock(Customer.class).;

    Customer customer = server.createEntityBean(Customer.class);
    customer.setId(42L);

    verify(customer).setId(42L);

    //assertThat(customer.getId()).isEqualTo(42L);
  }

  @Test
  public void testGetName() throws Exception {
    server.getName();
  }

  @Test
  public void testGetBeanId() throws Exception {
    server.getBeanId(null);
  }

  @Test
  public void testNextId() throws Exception {
    assertThat(server.nextId(null)).isEqualTo(server.nextId);
  }

  @Test
  public void testFindOne() throws Exception {
    server.findOne((Query) null, null);
  }

  @Test
  public void testExecute() throws Exception {
    server.execute((CallableSql) null);
  }

  @Test
  public void testExecute1() throws Exception {
    server.execute((SqlUpdate) null);
  }

  @Test
  public void testShutdown() throws Exception {
    server.shutdown(false, false);
  }

  @Test
  public void testGetExpressionFactory() throws Exception {
    server.getExpressionFactory();
  }

  @Test
  public void testGetMetaInfoManager() throws Exception {
    server.getMetaInfoManager();
  }

  @Test
  public void testGetBeanState() throws Exception {
    server.getBeanState(null);
  }

  @Test
  public void testDiff() throws Exception {
    assertThat(server.diff(null, null)).isEmpty();
  }

  @Test
  public void testCreateEntityBean1() throws Exception {
    Customer customer = server.createEntityBean(Customer.class);
    assertThat(customer).isNotNull();
  }

  @Test
  public void testCreateCsvReader() throws Exception {
    assertThat(server.createCsvReader(Customer.class)).isNotNull();
  }

  @Test
  public void testCreateQuery() throws Exception {
    assertThat(server.createQuery(null)).isNotNull();
  }

  @Test
  public void testFind() throws Exception {
    assertThat(server.find(null)).isNotNull();
  }

  @Test
  public void testFilter() throws Exception {
    assertThat(server.filter(null)).isNotNull();
  }

  @Test
  public void testSort() throws Exception {
    server.sort(null, null);
  }

  @Test
  public void testCreateUpdate() throws Exception {
    assertThat(server.createUpdate(null, null)).isNotNull();
  }

  @Test
  public void testCreateSqlQuery() throws Exception {
    assertThat(server.createSqlQuery(null)).isNotNull();

  }

  @Test
  public void testCreateSqlUpdate() throws Exception {
    assertThat(server.createSqlUpdate(null)).isNotNull();
  }

  @Test
  public void testCreateCallableSql() throws Exception {
    assertThat(server.createCallableSql(null)).isNotNull();
  }

  @Test
  public void testRegister() throws Exception {
    server.register(null);
  }

  @Test
  public void testCreateTransaction() throws Exception {
    assertThat(server.createTransaction()).isNotNull();
    assertThat(server.createTransaction(null)).isNotNull();
  }

  @Test
  public void testBeginTransaction() throws Exception {
      assertThat(server.beginTransaction()).isNotNull();
    assertThat(server.beginTransaction((TxScope)null)).isNotNull();
    assertThat(server.beginTransaction((TxIsolation) null)).isNotNull();
  }

  @Test
  public void testCurrentTransaction() throws Exception {
    assertThat(server.currentTransaction()).isNotNull();
  }

  @Test
  public void testCommitTransaction() throws Exception {
    server.commitTransaction();
  }

  @Test
  public void testRollbackTransaction() throws Exception {
    server.rollbackTransaction();
  }

  @Test
  public void testEndTransaction() throws Exception {
server.endTransaction();
  }

  @Test
  public void testRefresh() throws Exception {
    server.refresh(null);
  }

  @Test
  public void testRefreshMany() throws Exception {
    server.refreshMany(null, null);
  }

  @Test
  public void testFind1() throws Exception {
    assertThat(server.find(Customer.class)).isNotNull();
  }

  @Test
  public void testGetReference() throws Exception {
    assertThat(server.getReference(Customer.class, null)).isNotNull();
  }

  @Test
  public void testFindCount() throws Exception {
    server.findCount(null, null);
  }

  @Test
  public void testFindIds() throws Exception {
    server.findIds(null, null);
  }

  @Test
  public void testFindEach() throws Exception {
    server.findEach((Query<?>)null, null, null);
  }

  @Test
  public void testFindEachWhile() throws Exception {
    server.findEachWhile((Query<?>)null, null, null);
  }

  @Test
  public void testFindList() throws Exception {
    server.findList((Query) null, null);
    server.findList((SqlQuery) null, null);
  }

  @Test
  public void testFindFutureCount() throws Exception {
    server.findFutureCount(null, null);
  }

  @Test
  public void testFindFutureIds() throws Exception {
    server.findFutureIds(null, null);
  }

  @Test
  public void testFindFutureList() throws Exception {
    server.findFutureList((Query) null, null);
  }

  @Test
  public void testFindSet() throws Exception {
    server.findSet((Query) null, null);
  }

  @Test
  public void testFindMap() throws Exception {
    server.findMap((Query) null, null);
  }

  @Test
  public void testFindList1() throws Exception {
    server.findList((Query) null, null);
    server.findList((SqlQuery) null, null);
  }

  @Test
  public void testExternalModification() throws Exception {
    server.externalModification(null, true, true, true);
  }

  @Test
  public void testMarkAsDirty() throws Exception {
    server.markAsDirty(new Customer());
  }

  @Test
  public void testSave() throws Exception {
  }

  @Test
  public void testDelete() throws Exception {

  }

  @Test
  public void testUpdate() throws Exception {

  }

  @Test
  public void testInsert() throws Exception {

  }

  @Test
  public void testDeleteManyToManyAssociations() throws Exception {

  }

  @Test
  public void testDeleteManyToManyAssociations1() throws Exception {

  }

  @Test
  public void testSaveManyToManyAssociations() throws Exception {

  }

  @Test
  public void testSaveManyToManyAssociations1() throws Exception {

  }

  @Test
  public void testSaveAssociation() throws Exception {

  }

  @Test
  public void testSaveAssociation1() throws Exception {

  }

  @Test
  public void testGetServerCacheManager() throws Exception {
    assertThat(server.getServerCacheManager()).isNotNull();
  }

  @Test
  public void testGetBackgroundExecutor() throws Exception {
    assertThat(server.getBackgroundExecutor()).isNotNull();
  }

  @Test
  public void testJson() throws Exception {
    assertThat(server.json()).isNotNull();
  }
}