package io.ebean.mocker.delegate;

import io.ebean.MergeOptions;
import io.ebean.Transaction;

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

  void updateAll(Collection<?> beans, Transaction transaction) throws OptimisticLockException;

  void insert(Object bean, Transaction t);

  void insertAll(Collection<?> beans, Transaction t);

  void merge(Object bean, MergeOptions options, Transaction t);
}
