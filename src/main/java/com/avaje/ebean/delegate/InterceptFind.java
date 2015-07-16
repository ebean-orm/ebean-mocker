package com.avaje.ebean.delegate;

import com.avaje.ebean.FutureIds;
import com.avaje.ebean.FutureList;
import com.avaje.ebean.FutureRowCount;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.QueryEachWhileConsumer;
import com.avaje.ebean.QueryIterator;
import com.avaje.ebean.QueryResultVisitor;
import com.avaje.ebean.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rob on 15/07/15.
 */
public interface InterceptFind {

  void refresh(Object bean);

  void refreshMany(Object bean, String propertyName);

  <T> T findUnique(Query<T> query, Transaction transaction);

  <T> T find(Class<T> beanType, Object id, Transaction transaction);

  <T> int findRowCount(Query<T> query, Transaction transaction);

  <T> List<Object> findIds(Query<T> query, Transaction transaction);

  <T> QueryIterator<T> findIterate(Query<T> query, Transaction transaction);

  <T> void findEach(Query<T> query, QueryEachConsumer<T> consumer, Transaction transaction);

  <T> void findEachWhile(Query<T> query, QueryEachWhileConsumer<T> consumer, Transaction transaction);

  <T> void findVisit(Query<T> query, QueryResultVisitor<T> visitor, Transaction transaction);

  <T> List<T> findList(Query<T> query, Transaction transaction);

  <T> FutureRowCount<T> findFutureRowCount(Query<T> query, Transaction transaction);

  <T> FutureIds<T> findFutureIds(Query<T> query, Transaction transaction);

  <T> FutureList<T> findFutureList(Query<T> query, Transaction transaction);

  <T> PagedList<T> findPagedList(Query<T> query, Transaction transaction, int pageIndex, int pageSize);

  <T> Set<T> findSet(Query<T> query, Transaction transaction);

  <T> Map<?, T> findMap(Query<T> query, Transaction transaction);
}
