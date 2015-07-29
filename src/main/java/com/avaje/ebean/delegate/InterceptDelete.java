package com.avaje.ebean.delegate;

import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Provides an adaption for for all deletes.
 */
public interface InterceptDelete {

  int delete(Class<?> beanType, Object id, Transaction transaction);

  void delete(Object bean, Transaction t) throws OptimisticLockException;

  int deleteAll(Collection<?> c) throws OptimisticLockException;

  void deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction);

  int deleteManyToManyAssociations(Object ownerBean, String propertyName);

  int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t);
}
