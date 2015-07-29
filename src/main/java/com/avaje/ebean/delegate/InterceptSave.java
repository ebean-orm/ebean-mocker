package com.avaje.ebean.delegate;

import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Provides an adaption layer for intercepting save methods.
 */
public interface InterceptSave {

  Object nextId(Class<?> beanType);

  void save(Object bean, Transaction transaction) throws OptimisticLockException;

  int saveAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException;

  void update(Object bean, Transaction t) throws OptimisticLockException;

  void update(Object bean, Transaction transaction, boolean deleteMissingChildren) throws OptimisticLockException;

  void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException;

  void insert(Object bean, Transaction t);

  void insertAll(Collection<?> beans, Transaction t);

  void saveManyToManyAssociations(Object ownerBean, String propertyName);

  void saveManyToManyAssociations(Object ownerBean, String propertyName, Transaction t);

  void saveAssociation(Object ownerBean, String propertyName);

  void saveAssociation(Object ownerBean, String propertyName, Transaction t);
}
