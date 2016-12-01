package com.avaje.ebean.delegate;

import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Interception for SqlQuery.
 */
public interface InterceptFindSqlQuery {

  List<SqlRow> findList(SqlQuery query, Transaction transaction);

  SqlRow findUnique(SqlQuery query, Transaction transaction);

  void findEach(SqlQuery sqlQuery, Consumer<SqlRow> consumer, Transaction transaction);

  void findEachWhile(SqlQuery sqlQuery, Predicate<SqlRow> consumer, Transaction transaction);
}
