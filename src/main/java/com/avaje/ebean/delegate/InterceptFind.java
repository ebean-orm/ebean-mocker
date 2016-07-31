package com.avaje.ebean.delegate;

import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.QueryEachWhileConsumer;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Version;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides an adaption layer for find methods.
 */
public interface InterceptFind {

  void refresh(Object bean);

  void refreshMany(Object bean, String propertyName);

  <T> T findUnique(Query<T> query, Transaction transaction);

  <T> T find(Class<T> beanType, Object id, Transaction transaction);

  <T> int findCount(Query<T> query, Transaction transaction);

  <T> List<Object> findIds(Query<T> query, Transaction transaction);

  <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction);

  <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction);

  <T> List<T> findList(Query<T> query, Transaction transaction);

  <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction);

  <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction);

  <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction);

  <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction);

  <T> Set<T> findSet(Query<T> query, Transaction transaction);

  <T> Map<?, T> findMap(Query<T> query, Transaction transaction);

  <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction);

}