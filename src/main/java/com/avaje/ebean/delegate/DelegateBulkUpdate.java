package com.avaje.ebean.delegate;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Update;

/**
 * Created by rob on 15/07/15.
 */
public class DelegateBulkUpdate implements InterceptBulkUpdate {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateBulkUpdate(EbeanServer delegate) {
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

//  @Override
//  public void bulkUpdate(TxScope scope, TxRunnable r) {
//    delegate.bulkUpdate(scope, r);
//  }
//
//  @Override
//  public void bulkUpdate(TxRunnable r) {
//    delegate.bulkUpdate(r);
//  }
//
//  @Override
//  public <T> T bulkUpdate(TxScope scope, TxCallable<T> c) {
//    return delegate.bulkUpdate(scope, c);
//  }
//
//  @Override
//  public <T> T bulkUpdate(TxCallable<T> c) {
//    return delegate.bulkUpdate(c);
//  }

}
