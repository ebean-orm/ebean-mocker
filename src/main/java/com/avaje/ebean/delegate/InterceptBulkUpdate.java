package com.avaje.ebean.delegate;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Update;

/**
 * Created by rob on 15/07/15.
 */
public interface InterceptBulkUpdate {

  void externalModification(String tableName, boolean inserted, boolean updated, boolean deleted);

  int execute(SqlUpdate sqlUpdate);

  int execute(Update<?> update);

  int execute(Update<?> update, Transaction t);

  int execute(CallableSql callableSql);

  int execute(SqlUpdate updSql, Transaction t);

  int execute(CallableSql callableSql, Transaction t);

//  void bulkUpdate(TxScope scope, TxRunnable r);
//
//  void bulkUpdate(TxRunnable r);
//
//  <T> T bulkUpdate(TxScope scope, TxCallable<T> c);
//
//  <T> T bulkUpdate(TxCallable<T> c);
}
