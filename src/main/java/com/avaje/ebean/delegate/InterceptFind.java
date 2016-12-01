package com.avaje.ebean.delegate;

import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Version;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Provides an adaption layer for find methods.
 */
public interface InterceptFind {

  void refresh(Object bean);

  void refreshMany(Object bean, String propertyName);

  <T> T findUnique(Query<T> query, Transaction transaction);

  <T> T find(Class<T> beanType, Object id, Transaction transaction);

  <T> int findCount(Query<T> query, Transaction transaction);

  <A> List<A> findIds(Query<?> query, Transaction transaction);

  <T> void findEach(Query<T> query, Consumer<T> consumer, Transaction transaction);

  <T> void findEachWhile(Query<T> query, Predicate<T> consumer, Transaction transaction);

  <T> List<T> findList(Query<T> query, Transaction transaction);

  <T> FutureRowCount<T> findFutureCount(Query<T> query, Transaction transaction);

  <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction);

  <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction);

  <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction);

  <T> Set<T> findSet(Query<T> query, Transaction transaction);

  <K, T> Map<K, T> findMap(Query<T> query, Transaction transaction);

  <T> List<Version<T>> findVersions(Query<T> query, Transaction transaction);

  <A> List<A> findSingleAttributeList(Query<?> query, Transaction transaction);

  <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction);
}