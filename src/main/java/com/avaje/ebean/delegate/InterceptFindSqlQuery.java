package com.avaje.ebean.delegate;

import com.avaje.ebean.SqlFutureList;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rob on 15/07/15.
 */
public interface InterceptFindSqlQuery {

  List<SqlRow> findList(SqlQuery query, Transaction transaction);

  Set<SqlRow> findSet(SqlQuery query, Transaction transaction);

  Map<?, SqlRow> findMap(SqlQuery query, Transaction transaction);

  SqlRow findUnique(SqlQuery query, Transaction transaction);

  SqlFutureList findFutureList(SqlQuery query, Transaction transaction);
}
