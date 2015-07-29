package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rob on 15/07/15.
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
  public int delete(Class<?> beanType, Object id, Transaction transaction) {
    return delegate.delete(beanType, id, transaction);
  }

  @Override
  public void deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction) {
    delegate.deleteAll(beanType, ids, transaction);
  }

  @Override
  public void delete(Object bean, Transaction t) throws OptimisticLockException {
    delegate.delete(bean, t);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    return delegate.deleteManyToManyAssociations(ownerBean, propertyName, t);
  }

}
