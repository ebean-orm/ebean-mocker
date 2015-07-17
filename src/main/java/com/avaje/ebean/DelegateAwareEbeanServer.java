package com.avaje.ebean;

/**
 * Test EbeanServer implementations that want to delegate some functionality
 * through to a fully functional EbeanServer (that is typically using a test db like h2)
 */
public interface DelegateAwareEbeanServer {

  /**
   * Set the delegate if it has not already been set.
   * <p/>
   * This is typically used as part of MockiEbean run in order to support delegating
   * through to the original default ebeanServer.
   */
  boolean withDelegateIfRequired(EbeanServer delegate);

  /**
   * Called prior to run and typically used to set test doubles to static finders.
   */
  void beforeRun();

  /**
   * Called after run and typically used to restore static finders (from their test doubles).
   */
  void afterRun();
}
