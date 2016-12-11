package io.ebean;


import java.util.concurrent.Callable;

/**
 * This is a test helper class that can be used to swap out the default EbeanServer in the Ebean
 * singleton with a mock implementation.
 * <p>
 * This enables a developer to write a test using a tool like Mockito to mock the EbeanServer
 * interface and make this the default server of the Ebean singleton.
 * <p>
 * <p>
 *
 * </p>
 * <pre>{@code
 *
 *  EbeanServer mock = ...; // create a mock or test double etc
 *
 *  MockiEbean.runWithMock(mock, new Runnable() {
 *
 *    public void run() {
 *      ...
 *      // test code in here runs with mock EbeanServer
 *    }
 *  });
 *
 * }</pre>
 * <p>
 * An example using Mockito to mock the getBeanId() method on EbeanServer.
 * <p>
 * <pre>{@code
 *
 * @Test
 * public void testWithMockito() {
 *
 *   EbeanServer defaultServer = Ebean.getServer(null);
 *   assertTrue("is a real EbeanServer", defaultServer instanceof DefaultServer);
 *
 *   Long magicBeanId = Long.valueOf(47L);
 *
 *   EbeanServer mock = Mockito.mock(EbeanServer.class);
 *   when(mock.getBeanId(null)).thenReturn(magicBeanId);
 *
 *   MockiEbean mockiEbean = MockiEbean.start(mock);
 *   try {
 *
 *     // So using the Ebean singleton returns the mock instance
 *     EbeanServer server = Ebean.getServer(null);
 *     Object beanId = server.getBeanId(null);
 *
 *     assertEquals(magicBeanId, beanId);
 *
 *   } finally {
 *     mockiEbean.restoreOriginal();
 *   }
 *
 *   EbeanServer restoredServer = Ebean.getServer(null);
 *   assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
 * }
 *
 * }</pre>
 */
public class MockiEbean {

  /**
   * Set a mock implementation of EbeanServer as the default server.
   * <p>
   * Typically the mock instance passed in is created by Mockito or similar tool.
   * <p>
   * The default EbeanSever is the instance returned by {@link Ebean#getServer(String)} when the
   * server name is null.
   *
   * @param mock the mock instance that becomes the default EbeanServer
   * @return The MockiEbean with a {@link #restoreOriginal()} method that can be used to restore the
   * original EbeanServer implementation.
   */
  public static MockiEbean start(EbeanServer mock) {

    // using $mock as the server name
    EbeanServer original = Ebean.mock("$mock", mock, true);

    if (mock instanceof DelegateAwareEbeanServer) {
      ((DelegateAwareEbeanServer)mock).withDelegateIfRequired(original);
    }

    return new MockiEbean(mock, original);
  }

  /**
   * Run the test runnable using the mock EbeanServer and restoring the original EbeanServer afterward.
   *
   * @param mock the mock instance that becomes the default EbeanServer
   * @param test typically some test code as a runnable
   */
  public static void runWithMock(EbeanServer mock, Runnable test) {

    start(mock).run(test);
  }

  /**
   * Run the test runnable using the mock EbeanServer and restoring the original EbeanServer afterward.
   *
   * @param mock the mock instance that becomes the default EbeanServer
   * @param test typically some test code as a callable
   */
  public static <V> V runWithMock(EbeanServer mock, Callable<V> test) throws Exception {

    return start(mock).run(test);
  }

  /**
   * The 'original' default EbeanServer that the mock is temporarily replacing.
   */
  protected final EbeanServer original;

  /**
   * The 'mock' EbeanServer that is temporarily replacing the 'original' during the 'run'.
   */
  protected final EbeanServer mock;

  /**
   * Construct with the mock and original EbeanServer instances.s
   */
  protected MockiEbean(EbeanServer mock, EbeanServer original) {
    this.mock = mock;
    this.original = original;
  }

  /**
   * Return the original EbeanServer implementation.
   * <p>
   * This is the implementation that is put back as the default EbeanServer when
   * {@link #restoreOriginal()} is called.
   */
  public EbeanServer getOriginal() {
    return original;
  }

  /**
   * Return the mock EbeanServer instance that was set as the default EbeanServer.
   */
  public EbeanServer getMock() {
    return mock;
  }

  /**
   * Run the test runnable restoring the original EbeanServer afterwards.
   */
  public void run(Runnable testRunnable) {
    try {
      beforeRun();
      testRunnable.run();
    } finally {
      afterRun();
      restoreOriginal();
    }
  }


  /**
   * Run the test callable restoring the original EbeanServer afterwards.
   */
  public <V> V run(Callable<V> testCallable) throws Exception {
    try {
      beforeRun();
      return testCallable.call();
    } finally {
      afterRun();
      restoreOriginal();
    }
  }


  /**
   * Typically only used internally.
   *
   * Usually used to set static Finder test doubles.
   */
  public void beforeRun() {
    if (mock instanceof DelegateAwareEbeanServer) {
      ((DelegateAwareEbeanServer)mock).beforeRun();
    }
  }

  /**
   * Typically only used internally.
   *
   * Usually used to restore static Finder original implementations.
   */
  public void afterRun() {
    if (mock instanceof DelegateAwareEbeanServer) {
      ((DelegateAwareEbeanServer)mock).afterRun();
    }
  }

  /**
   * Restore the original EbeanServer implementation as the default EbeanServer.
   */
  public void restoreOriginal() {
    if (original == null) {
      throw new IllegalStateException("Original EbeanServer instance is null");
    }
    if (original.getName() == null) {
      throw new IllegalStateException("Original EbeanServer name is null");
    }

    // restore the original EbeanServer back
    Ebean.mock(original.getName(), original, true);
  }

}
