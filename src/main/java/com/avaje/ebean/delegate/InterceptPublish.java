package com.avaje.ebean.delegate;

import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;

import java.util.List;

/**
 * Created by rob on 13/01/16.
 */
public interface InterceptPublish {

  <T> T publish(Class<T> beanType, Object id, Transaction transaction);

  <T> T publish(Class<T> beanType, Object id);

  <T> List<T> publish(Query<T> query, Transaction transaction);

  <T> List<T> publish(Query<T> query);

  <T> T draftRestore(Class<T> beanType, Object id, Transaction transaction);

  <T> T draftRestore(Class<T> beanType, Object id);

  <T> List<T> draftRestore(Query<T> query, Transaction transaction);

  <T> List<T> draftRestore(Query<T> query);
}
