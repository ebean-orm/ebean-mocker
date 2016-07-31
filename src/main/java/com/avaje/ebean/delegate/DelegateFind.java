package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.QueryEachWhileConsumer;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Version;
import com.avaje.ebeaninternal.api.SpiQuery;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wraps an underlying EbeanServer.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegateFind implements InterceptFind {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delegate to by default.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateFind(EbeanServer delegate) {
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
  public <T> T findUnique(Query<T> query, Transaction transaction) {
    return delegate.findUnique(query, transaction);
  }

  @Override
  public <T> T find(Class<T> beanType, Object id, Transaction transaction) {
    return delegate.find(beanType, id, transaction);
  }

  @Override
  public <T> int findCount(Query<T> query, Transaction transaction) {
    return delegate.findCount(query, transaction);
  }

  @Override
  public <T> List<Object> findIds(Query<T> query, Transaction transaction) {
    return delegate.findIds(query, transaction);
  }

  @Override
  public <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction) {
    delegate.findEach(query, consumer, transaction);
  }

  @Override
  public <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction) {
    delegate.findEachWhile(query, consumer, transaction);
  }

  @Override
  public <T> List<T> findList(Query<T> query, Transaction transaction) {

    SpiQuery q = (SpiQuery)query;
    q.getBeanType();
    return delegate.findList(query, transaction);
  }

  @Override
  public <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction) {
    return delegate.findFutureCount(query, transaction);
  }

  @Override
  public <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction) {
    return delegate.findFutureIds(query, transaction);
  }

  @Override
  public <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction) {
    return delegate.findFutureList(query, transaction);
  }

  @Override
  public <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction) {
    return delegate.findPagedList(query, transaction);
  }

  @Override
  public <T> Set<T> findSet(Query<T> query, Transaction transaction) {
    return delegate.findSet(query, transaction);
  }

  @Override
  public <T> Map<?, T> findMap(Query<T> query, Transaction transaction) {
    return delegate.findMap(query, transaction);
  }

  @Override
  public <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction) {
    return delegate.findVersions(query, transaction);
  }
}
