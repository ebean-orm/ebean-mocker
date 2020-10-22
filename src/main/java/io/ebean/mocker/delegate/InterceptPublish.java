package io.ebean.mocker.delegate;

import io.ebean.Query;
import io.ebean.Transaction;

import java.util.List;

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
