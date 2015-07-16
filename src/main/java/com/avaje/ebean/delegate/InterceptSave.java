package com.avaje.ebean.delegate;

import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rob on 15/07/15.
 */
public interface InterceptSave {

  Object nextId(Class<?> beanType);

  void save(Object bean, Transaction transaction) throws OptimisticLockException;

  int save(Iterator<?> it, Transaction transaction) throws OptimisticLockException;

  int save(Collection<?> beans, Transaction transaction) throws OptimisticLockException;

  void update(Object bean, Transaction t) throws OptimisticLockException;

  void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException;

  void update(Collection<?> beans, Transaction transaction) throws OptimisticLockException;

  void insert(Object bean, Transaction t);

  void insert(Collection<?> beans, Transaction t);

  void saveManyToManyAssociations(Object ownerBean, String propertyName);

  void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction t);

  void saveAssociation(Object ownerBean, String propertyName);

  void saveAssociation(Object ownerBean, String propertyName, Transaction t);
}
