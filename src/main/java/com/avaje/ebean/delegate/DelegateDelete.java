package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;

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
  public void deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    delegate.deleteAll(beanType, ids, transaction);
  }

  @Override
  public boolean delete(Object bean, Transaction t) throws OptimisticLockException {
    return delegate.delete(bean, t);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName, t);
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
}
