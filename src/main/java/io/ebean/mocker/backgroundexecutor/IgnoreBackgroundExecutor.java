package io.ebean.mocker.backgroundexecutor;

import io.ebean.BackgroundExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
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
  public <T> Future<T> submit(Callable<T> callable) { return null; }

  @Override
  public Future<?> submit(Runnable runnable) { return null; }

  @Override
  public void execute(Runnable r) {
    // just ignore
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long l, long l1, TimeUnit timeUnit) { return null; }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long l, long l1, TimeUnit timeUnit) { return null; }

  @Override
  public ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
    return null;
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> c, long delay, TimeUnit unit) {
    return null;
  }

}
