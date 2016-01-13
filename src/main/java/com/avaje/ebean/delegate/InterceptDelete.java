package com.avaje.ebean.delegate;

import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;

/**
 * Provides an adaption for for all deletes.
 */
public interface InterceptDelete {

  int delete(Class<?> beanType, Object id, Transaction transaction);

  boolean delete(Object bean, Transaction t) throws OptimisticLockException;

  int deleteAll(Collection<?> c) throws OptimisticLockException;

  int deleteAll(Collection<?> c, Transaction transaction) throws OptimisticLockException;

  int delete(Query<?> query, Transaction transaction) throws OptimisticLockException;

  void deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction);

  int deleteManyToManyAssociations(Object ownerBean, String propertyName);

  int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t);

  boolean deletePermanent(Object bean);

  boolean deletePermanent(Object bean, Transaction transaction);

  int deleteAllPermanent(Collection<?> beans);

  int deleteAllPermanent(Collection<?> beans, Transaction transaction);
}
