package io.ebean.mocker.delegate;

import io.ebean.Database;
import io.ebean.DtoQuery;
import io.ebean.Filter;
import io.ebean.Query;
import io.ebean.SqlQuery;

import java.util.Set;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegateQuery {

  protected Database delegate;

  protected Database owner;

  /**
   * Construct with a EbeanServer to delegate to by default.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateQuery(Database delegate, Database owner) {
    this.delegate = delegate;
    this.owner = owner;
  }

  public <T> Query<T> delegateToThisServer(Query<T> sourceQuery) {

    return DelegateOrmQuery.copy(sourceQuery, owner);
  }

  public <T> Query<T> findNative(Class<T> beanType, String nativeSql) {
    return delegateToThisServer(delegate.findNative(beanType, nativeSql));
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

  public SqlQuery sqlQuery(String sql) {
    return delegate.sqlQuery(sql);
  }

  public <T> T reference(Class<T> beanType, Object id) {
    return delegate.reference(beanType, id);
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

  public <T> DtoQuery<T> findDto(Class<T> dtoType, String sql) {
    return delegate.findDto(dtoType, sql);
  }

  public <T> DtoQuery<T> createNamedDtoQuery(Class<T> dtoType, String namedQuery) {
    return delegate.createNamedDtoQuery(dtoType, namedQuery);
  }
}
