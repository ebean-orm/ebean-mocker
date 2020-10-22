package io.ebean.delegate;

import io.ebean.CallableSql;
import io.ebean.Database;
import io.ebean.SqlUpdate;
import io.ebean.Transaction;
import io.ebean.Update;

public class DelegateBulkUpdate implements InterceptBulkUpdate {

  protected Database delegate;

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateBulkUpdate(Database delegate) {
    this.delegate = delegate;
  }

  @Override
  public void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted) {
    delegate.externalModification(tableName, inserted, updated, deleted);
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
  public int execute(SqlUpdate updSql, Transaction t) {
    return delegate.execute(updSql, t);
  }

  @Override
  public int execute(CallableSql callableSql, Transaction t) {
    return delegate.execute(callableSql, t);
  }

}
