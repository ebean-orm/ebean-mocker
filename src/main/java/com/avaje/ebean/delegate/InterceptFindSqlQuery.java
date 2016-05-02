package com.avaje.ebean.delegate;

import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.QueryEachWhileConsumer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

import java.util.List;

/**
 * Interception for SqlQuery.
 */
public interface InterceptFindSqlQuery {

  List<SqlRow> findList(SqlQuery query, Transaction transaction);

  SqlRow findUnique(SqlQuery query, Transaction transaction);

  void findEach(SqlQuery sqlQuery, QueryEachConsumer<SqlRow> consumer, Transaction transaction);

  void findEachWhile(SqlQuery sqlQuery, QueryEachWhileConsumer<SqlRow> consumer, Transaction transaction);
}
