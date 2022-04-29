package io.ebean.mocker.delegate;

import io.ebean.Database;
import io.ebean.FutureIds;
import io.ebean.FutureList;
import io.ebean.FutureRowCount;
import io.ebean.PagedList;
import io.ebean.Query;
import io.ebean.QueryIterator;
import io.ebean.Transaction;
import io.ebean.Version;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegateFind implements InterceptFind {

  protected Database delegate;

  /**
   * Construct with a EbeanServer to delegate to by default.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateFind(Database delegate) {
    this.delegate = delegate;
  }

  @Override
  public void refresh(Object bean) {
    delegate.refresh(bean);
  }

  @Override
  public void refreshMany(Object bean, String propertyName) {
    delegate.refreshMany(bean, propertyName);
  }

  @Override
  public <T> T findOne(Query<T> query, Transaction transaction) {
    return delegate.extended().findOne(query, transaction);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id, Transaction transaction) {
    return delegate.find(beanType, id, transaction);
  }

  @Override
  public <T> int findCount(Query<T> query, Transaction transaction) {
    return delegate.extended().findCount(query, transaction);
  }

  @Override
  public <A> List<A> findIds(Query<?> query, Transaction transaction) {
    return delegate.extended().findIds(query, transaction);
  }

  @Override
  public <A> List<A> findSingleAttributeList(Query<?> query, Transaction transaction) {
    return delegate.extended().findSingleAttributeList(query, transaction);
  }

  @Override
  public <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction) {
    return delegate.extended().findIterate(query, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, Consumer<T> consumer, Transaction transaction) {
    delegate.extended().findEach(query, consumer, transaction);
  }

  @Override
  public <T> void findEachWhile(Query<T> query, Predicate<T> consumer, Transaction transaction) {
    delegate.extended().findEachWhile(query, consumer, transaction);
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {
    return delegate.extended().findList(query, transaction);
  }

  @Override
  public <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction) {
    return delegate.extended().findFutureCount(query, transaction);
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    return delegate.extended().findFutureIds(query, transaction);
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    return delegate.extended().findFutureList(query, transaction);
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction) {
    return delegate.extended().findPagedList(query, transaction);
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    return delegate.extended().findSet(query, transaction);
  }

  @Override
  public <K, T> Map<K, T> findMap(Query<T> query, Transaction transaction) {
    return delegate.extended().findMap(query, transaction);
  }

  @Override
  public <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction) {
    return delegate.extended().findVersions(query, transaction);
  }

  @Override
  public <T> Stream<T> findStream(Query<T> query, Transaction transaction) {
    return delegate.extended().findStream(query, transaction);
  }

  @Override
  public boolean exists(Query<?> query, Transaction transaction) {
    return delegate.extended().exists(query, transaction);
  }
}
