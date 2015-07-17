package com.avaje.ebean;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the set of WithStaticFinder.
 *
 */
public class WithStaticFinders {

  protected List<WithStaticFinder<?>> staticFinderReplacement = new ArrayList<WithStaticFinder<?>>();

  /**
   * Create and return a WithStaticFinder. Expects a static 'finder' field on the beanType class.
   */
  public <T> WithStaticFinder<T> withFinder(Class<T> beanType) {
    WithStaticFinder withFinder = new WithStaticFinder<T>(beanType);
    staticFinderReplacement.add(withFinder);
    return withFinder;
  }

  /**
   * Use test doubles replacing original implementations.
   */
  public void useTestDoubles() {

    for (WithStaticFinder<?> withStaticFinder : staticFinderReplacement) {
      withStaticFinder.useTestDouble();
    }
  }

  /**
   * Restore the original implementations.
   */
  public void restoreOriginal() {

    for (WithStaticFinder<?> withStaticFinder : staticFinderReplacement) {
      withStaticFinder.restoreOriginal();
    }
  }

  /**
   * Before the start of a MockiEbean run calls useTestDoubles().
   */
  public void beforeRun() {
    useTestDoubles();
  }

  /**
   * After the end of a MockiEbean run calls restoreOriginal().
   */
  public void afterRun() {
    restoreOriginal();
  }
}
