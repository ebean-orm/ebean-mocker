package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rob on 15/07/15.
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
  public int save(Iterator<?> it, Transaction transaction) throws OptimisticLockException {
    return delegate.save(it, transaction);
  }

  @Override
  public int save(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    return delegate.save(beans, transaction);
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
  public void update(Collection<?> beans, Transaction transaction) throws OptimisticLockException {
    delegate.update(beans, transaction);
  }

  @Override
  public void insert(Object bean, Transaction t) {
    delegate.insert(bean, t);
  }

  @Override
  public void insert(Collection<?> beans, Transaction t) {
    delegate.insert(beans, t);
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName) {
    delegate.saveManyToManyAssociations(ownerBean, propertyName);
  }

  @Override
  public void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction t) {
    delegate.saveManyToManyAssociations(ownerBean, propertyName, t);
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName) {
    delegate.saveAssociation(ownerBean, propertyName);
  }

  @Override
  public void saveAssociation(Object ownerBean, String propertyName, Transaction t) {
    delegate.saveAssociation(ownerBean, propertyName, t);
  }


}
