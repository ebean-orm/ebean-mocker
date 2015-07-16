package com.avaje.ebean.backgroundexecutor;

import com.avaje.ebean.BackgroundExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Implementation that ignores execution requests.
 * <p>
 * In the case of running tests usually you want the background task
 * to bulkUpdate immediately or not at all.
 */
public class IgnoreBackgroundExecutor implements BackgroundExecutor {

  @Override
  public void execute(Runnable r) {
    // just ignore
  }

  @Override
  public void executePeriodically(Runnable r, long delay, TimeUnit unit) {
    // just ignore
  }
  
}
