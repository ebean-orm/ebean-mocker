package io.ebean.backgroundexecutor;

import io.ebean.BackgroundExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Implementation that immediately executes.
 * <p>
 * In the case of running tests usually you want the background task
 * to bulkUpdate immediately or not at all.
 */
public class ImmediateBackgroundExecutor implements BackgroundExecutor {

  @Override
  public void execute(Runnable r) {
    r.run();
  }

  @Override
  public void executePeriodically(Runnable r, long delay, TimeUnit unit) {
    r.run();
  }

}
