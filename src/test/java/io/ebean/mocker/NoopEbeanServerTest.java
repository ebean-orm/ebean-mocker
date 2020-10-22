package io.ebean.mocker;

import io.ebean.CallableSql;
import io.ebean.SqlUpdate;
import io.ebean.TxScope;
import io.ebean.annotation.TxIsolation;
import io.ebean.mocker.NoopEbeanServer;
import org.assertj.core.api.StrictAssertions;
import org.example.domain.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

public class NoopEbeanServerTest {

  private NoopEbeanServer server = new NoopEbeanServer();

  @Test
  public void testCreateEntityBean() {

    //Mockito.mock(Customer.class).;

    Customer customer = server.createEntityBean(Customer.class);
    customer.setId(42L);

    verify(customer).setId(42L);

    //assertThat(customer.getId()).isEqualTo(42L);
  }

  @Test
  public void testGetName() {
    server.getName();
  }

  @Test
  public void testGetBeanId() {
    server.getBeanId(null);
  }

  @Test
  public void testNextId() {
    assertThat(server.nextId(null)).isEqualTo(server.nextId);
  }

  @Test
  public void testExecute() {
    server.execute((CallableSql) null);
  }

  @Test
  public void testExecute1() {
    server.execute((SqlUpdate) null);
  }

  @Test
  public void testShutdown() {
    server.shutdown(false, false);
  }

  @Test
  public void testGetExpressionFactory() {
    server.getExpressionFactory();
  }

  @Test
  public void testGetMetaInfoManager() {
    server.getMetaInfoManager();
  }

  @Test
  public void testGetBeanState() {
    server.getBeanState(null);
  }

  @Test
  public void testDiff() {
    assertThat(server.diff(null, null)).isEmpty();
  }

  @Test
  public void testCreateEntityBean1() {
    Customer customer = server.createEntityBean(Customer.class);
    assertThat(customer).isNotNull();
  }

  @Test
  public void testCreateCsvReader() {
    assertThat(server.createCsvReader(Customer.class)).isNotNull();
  }

  @Test
  public void testCreateQuery() {
    assertThat(server.createQuery(null)).isNotNull();
  }

  @Test
  public void testFind() {
    assertThat(server.find(null)).isNotNull();
  }

  @Test
  public void testFilter() {
    assertThat(server.filter(null)).isNotNull();
  }

  @Test
  public void testSort() {
    server.sort(null, null);
  }

  @Test
  public void testCreateUpdate() {
    assertThat(server.createUpdate(null, null)).isNotNull();
  }

  @Test
  public void testCreateSqlQuery() {
    assertThat(server.createSqlQuery(null)).isNotNull();

  }

  @Test
  public void testCreateSqlUpdate() {
    assertThat(server.createSqlUpdate(null)).isNotNull();
  }

  @Test
  public void testCreateCallableSql() {
    assertThat(server.createCallableSql(null)).isNotNull();
  }

  @Test
  public void testRegister() {
    server.register(null);
  }

  @Test
  public void testCreateTransaction() {
    assertThat(server.createTransaction()).isNotNull();
    assertThat(server.createTransaction(null)).isNotNull();
  }

  @Test
  public void testBeginTransaction() {
      assertThat(server.beginTransaction()).isNotNull();
    StrictAssertions.assertThat(server.beginTransaction((TxScope)null)).isNotNull();
    assertThat(server.beginTransaction((TxIsolation) null)).isNotNull();
  }

  @Test
  public void testCurrentTransaction() {
    assertThat(server.currentTransaction()).isNotNull();
  }

  @Test
  public void testCommitTransaction() {
    server.commitTransaction();
  }

  @Test
  public void testRollbackTransaction() {
    server.rollbackTransaction();
  }

  @Test
  public void testEndTransaction() {
server.endTransaction();
  }

  @Test
  public void testRefresh() {
    server.refresh(null);
  }

  @Test
  public void testRefreshMany() {
    server.refreshMany(null, null);
  }

  @Test
  public void testFind1() {
    assertThat(server.find(Customer.class)).isNotNull();
  }

  @Test
  public void testGetReference() {
    assertThat(server.getReference(Customer.class, null)).isNotNull();
  }

  @Test
  public void testExternalModification() {
    server.externalModification(null, true, true, true);
  }

  @Test
  public void testMarkAsDirty() {
    server.markAsDirty(new Customer());
  }

  @Test
  public void testSave() {
  }

  @Test
  public void testDelete() {

  }

  @Test
  public void testUpdate() {

  }

  @Test
  public void testInsert() {

  }

  @Test
  public void testDeleteManyToManyAssociations() {

  }

  @Test
  public void testDeleteManyToManyAssociations1() {

  }

  @Test
  public void testSaveManyToManyAssociations() {

  }

  @Test
  public void testSaveManyToManyAssociations1() {

  }

  @Test
  public void testSaveAssociation() {

  }

  @Test
  public void testSaveAssociation1() {

  }

  @Test
  public void testGetServerCacheManager() {
    assertThat(server.getServerCacheManager()).isNotNull();
  }

  @Test
  public void testGetBackgroundExecutor() {
    assertThat(server.getBackgroundExecutor()).isNotNull();
  }

  @Test
  public void testJson() {
    assertThat(server.json()).isNotNull();
  }
}
