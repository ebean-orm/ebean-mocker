package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Filter;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;

import java.util.Set;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegateQuery {

  protected EbeanServer delegate;

  protected EbeanServer owner;

  /**
   * Construct with a EbeanServer to delegate to by default.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateQuery(EbeanServer delegate, EbeanServer owner) {
    this.delegate = delegate;
    this.owner = owner;
  }

  public <T> Query<T> delegateToThisServer(Query<T> sourceQuery) {

    return DelegateOrmQuery.copy(sourceQuery, owner);
  }

  public <T> Query<T> createQuery(Class<T> beanType) {
    return delegateToThisServer(delegate.createQuery(beanType));
  }

  public <T> Query<T> find(Class<T> beanType) {
    return delegateToThisServer(delegate.find(beanType));
  }

  public <T> Filter<T> filter(Class<T> beanType) {
    return delegate.filter(beanType);
  }

  public SqlQuery createSqlQuery(String sql) {
    return delegate.createSqlQuery(sql);
  }

  public <T> T getReference(Class<T> beanType, Object id) {
    return delegate.getReference(beanType, id);
  }

  public <T> Set<String> validateQuery(Query<T> query) {
    return delegate.validateQuery(query);
  }

  public <T> Query<T> createQuery(Class<T> beanType, String eql) {
    return delegate.createQuery(beanType, eql);
  }

  public <T> Query<T> createNamedQuery(Class<T> beanType, String namedQuery) {
    return delegate.createNamedQuery(beanType, namedQuery);
  }
}
