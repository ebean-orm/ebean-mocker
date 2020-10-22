package io.ebean.delegate;

import io.ebean.Database;
import io.ebean.MergeOptions;
import io.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Delegate based InterceptSave implementation.
 */
public class DelegateSave implements InterceptSave {

  protected Database delegate;

  /**
   * Construct with a EbeanServer to delegate and using ImmediateBackgroundExecutor.
   * <p>
   * This delegate will be used on all method calls that are not overwritten.
   */
  public DelegateSave(Database delegate) {
    this.delegate = delegate;
  }

  @Override
  public Object nextId(Class<?> beanType) {
    return delegate.nextId(beanType);
  }

  @Override
  public void merge(Object bean, MergeOptions options, Transaction t) {
    delegate.merge(bean, options, t);
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
