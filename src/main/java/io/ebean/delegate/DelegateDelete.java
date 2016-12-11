package io.ebean.delegate;

import io.ebean.EbeanServer;
import io.ebean.Query;
import io.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Simple delegating implementation of InterceptDelete.
 */
public class DelegateDelete implements InterceptDelete {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateDelete(EbeanServer delegate) {
    this.delegate = delegate;
  }

  @Override
  public int deleteAll(Collection<?> c) throws OptimisticLockException {
    return delegate.deleteAll(c);
  }

  @Override
  public int deleteAll(Collection<?> c, Transaction transaction) throws OptimisticLockException {
    return delegate.deleteAll(c, transaction);
  }

  @Override
  public int delete(Query<?> query, Transaction transaction) throws OptimisticLockException {
    return delegate.delete(query, transaction);
  }

  @Override
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    return delegate.delete(beanType, id, transaction);
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id) {
    return delegate.deletePermanent(beanType, id);
  }

  @Override
  public int deletePermanent(Class<?> beanType, Object id, Transaction transaction) {
    return delegate.deletePermanent(beanType, id, transaction);
  }

  @Override
  public int deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    return delegate.deleteAll(beanType, ids, transaction);
  }

  @Override
  public boolean delete(Object bean, Transaction t) throws OptimisticLockException {
    return delegate.delete(bean, t);
  }

  @Override
  public boolean deletePermanent(Object bean) {
    return delegate.deletePermanent(bean);
  }

  @Override
  public boolean deletePermanent(Object bean, Transaction transaction) {
    return delegate.deletePermanent(bean, transaction);
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans) {
    return delegate.deleteAllPermanent(beans);
  }

  @Override
  public int deleteAllPermanent(Collection<?> beans, Transaction transaction) {
    return delegate.deleteAllPermanent(beans, transaction);
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids) {
    return delegate.deleteAllPermanent(beanType, ids);
  }

  @Override
  public int deleteAllPermanent(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    return deleteAllPermanent(beanType, ids, transaction);
  }
}
