package io.ebean.mocker.backgroundexecutor;

import io.ebean.BackgroundExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
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

  @Override
  public void executePeriodically(Runnable r, long initialDelay, long delay, TimeUnit unit) {
    // just ignore
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
