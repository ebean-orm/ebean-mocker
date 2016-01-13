package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;

import java.util.List;

/**
 * Wraps an underlying EbeanServer providing the publishing features.
 * <p>
 * Can you used for testing purposes when you want to create a test double that
 * only replaces some of the underlying functionality of the EbeanServer.
 */
public class DelegatePublish implements InterceptPublish {

  protected EbeanServer delegate;

  public DelegatePublish(EbeanServer delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> T publish(Class<T> beanType, Object id, Transaction transaction) {
    return delegate.publish(beanType, id, transaction);
  }

  @Override
  public <T> T publish(Class<T> beanType, Object id) {
    return delegate.publish(beanType, id);
  }

  @Override
  public <T> List<T> publish(Query<T> query, Transaction transaction) {
    return delegate.publish(query, transaction);
  }

  @Override
  public <T> List<T> publish(Query<T> query) {
    return delegate.publish(query);
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id, Transaction transaction) {
    return delegate.draftRestore(beanType, id, transaction);
  }

  @Override
  public <T> T draftRestore(Class<T> beanType, Object id) {
    return delegate.draftRestore(beanType, id);
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query, Transaction transaction) {
    return delegate.draftRestore(query, transaction);
  }

  @Override
  public <T> List<T> draftRestore(Query<T> query) {
    return delegate.draftRestore(query);
  }
}
