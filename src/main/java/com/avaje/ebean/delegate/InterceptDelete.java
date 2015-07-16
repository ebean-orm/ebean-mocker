package com.avaje.ebean.delegate;

import com.avaje.ebean.Transaction;

import javax.persistence.OptimisticLockException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rob on 15/07/15.
 */
public interface InterceptDelete {

  int delete(Collection<?> c) throws OptimisticLockException;

  int delete(Class<?> beanType, Object id, Transaction transaction);

  void delete(Class<?> beanType, Collection<?> ids, Transaction transaction);

  void delete(Object bean, Transaction t) throws OptimisticLockException;

  int delete(Iterator<?> it, Transaction t) throws OptimisticLockException;

  int deleteManyToManyAssociations(Object ownerBean, String propertyName);

  int deleteManyToManyAssociations(Object ownerBean, String propertyName, Transaction t);
}
