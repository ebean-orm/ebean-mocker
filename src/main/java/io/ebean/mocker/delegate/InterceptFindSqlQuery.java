package io.ebean.mocker.delegate;

import io.ebean.SqlQuery;
import io.ebean.SqlRow;
import io.ebean.Transaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Interception for SqlQuery.
 */
public interface InterceptFindSqlQuery {

  List<SqlRow> findList(SqlQuery query, Transaction transaction);

  SqlRow findOne(SqlQuery query, Transaction transaction);

  void findEach(SqlQuery sqlQuery, Consumer<SqlRow> consumer, Transaction transaction);

  void findEachWhile(SqlQuery sqlQuery, Predicate<SqlRow> consumer, Transaction transaction);
}
