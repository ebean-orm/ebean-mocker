package io.ebean.mocker.backgroundexecutor;

import io.ebean.BackgroundExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
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

  @Override
  public void executePeriodically(Runnable r, long initialDelay, long delay, TimeUnit unit) {

  }

  @Override
  public ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
    return null;
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> c, long delay, TimeUnit unit) {
    return null;
  }

}
