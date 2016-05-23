package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Delegate based InterceptSave implementation.
 */
public class DelegateSave implements InterceptSave {

  protected EbeanServer delegate;

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateSave(EbeanServer delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object nextId(Class<?> beanType) {
    return delegate.nextId(beanType);
  }

  @Override
  public void save(Object bean, Transaction transaction) throws OptimisticLockException {
    delegate.save(bean, transaction);
  }

  @Override
  public int saveAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    return delegate.saveAll(beans, transaction);
  }

  @Override
  public void update(Object bean, Transaction t) throws OptimisticLockException {
    delegate.update(bean, t);
  }

  @Override
  public void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException {
    delegate.update(bean, transaction, deleteMissingChildren);
  }

  @Override
  public void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    delegate.updateAll(beans, transaction);
  }

  @Override
  public void insert(Object bean, Transaction t) {
    delegate.insert(bean, t);
  }

  @Override
  public void insertAll(Collection<?> beans, Transaction t) {
    delegate.insertAll(beans, t);
  }

}
