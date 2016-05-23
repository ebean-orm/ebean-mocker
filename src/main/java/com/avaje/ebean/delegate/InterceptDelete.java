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

  int deletePermanent(Class<?> beanType, Object id);

  int deletePermanent(Class<?> beanType, Object id, Transaction transaction);

  int deleteAll(Collection<?> c) throws OptimisticLockException;

  int deleteAll(Collection<?> c, Transaction transaction) throws OptimisticLockException;

  int delete(Query<?> query, Transaction transaction) throws OptimisticLockException;

  int deleteAll(Class<?> beanType, Collection<?> ids, Transaction transaction);

  int deleteAllPermanent(Class<?> beanType, Collection<?> ids);

  int deleteAllPermanent(Class<?> beanType, Collection<?> ids, Transaction transaction);

  boolean deletePermanent(Object bean);

  boolean deletePermanent(Object bean, Transaction transaction);

  int deleteAllPermanent(Collection<?> beans);

  int deleteAllPermanent(Collection<?> beans, Transaction transaction);
}
