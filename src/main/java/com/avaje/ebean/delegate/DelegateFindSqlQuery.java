package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlFutureList;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegateFindSqlQuery implements InterceptFindSqlQuery {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delegate to by default.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateFindSqlQuery(EbeanServer delegate) {
    this.delegate = delegate;
  }

  @Override
  public List<SqlRow> findList(SqlQuery query, Transaction transaction) {
    return delegate.findList(query, transaction);
  }

  @Override
  public Set<SqlRow> findSet(SqlQuery query, Transaction transaction) {
    return delegate.findSet(query, transaction);
  }

  @Override
  public Map<?, SqlRow> findMap(SqlQuery query, Transaction transaction) {
    return delegate.findMap(query, transaction);
  }

  @Override
  public SqlRow findUnique(SqlQuery query, Transaction transaction) {
    return delegate.findUnique(query, transaction);
  }

  @Override
  public SqlFutureList findFutureList(SqlQuery query, Transaction transaction) {
    return delegate.findFutureList(query, transaction);
  }
}
