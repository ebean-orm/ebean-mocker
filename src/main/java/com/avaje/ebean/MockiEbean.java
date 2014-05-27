package com.avaje.ebean;


/**
 * This is a test helper class that can be used to swap out the default EbeanServer in the Ebean
 * singleton with a mock implementation.
 * <p>
 * This enables a developer to write a test using a tool like Mockito to mock the EbeanServer
 * interface and make this the default server of the Ebean singleton.
 * 
 * <p>
 * An example using Mockito to mock the getBeanId() method on EbeanServer.
 * 
 * <pre>
 * &#064;Test
 * public void testWithMockito() {
 * 
 *   EbeanServer defaultServer = Ebean.getServer(null);
 *   assertTrue(&quot;is a real EbeanServer&quot;, defaultServer instanceof DefaultServer);
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
 *   assertTrue(&quot;is a real EbeanServer&quot;, restoredServer instanceof DefaultServer);
 * }
 * 
 * </pre>
 * 
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
   * @param mock
   *          the mock instance that becomes the default EbeanServer
   * 
   * @return The MockiEbean with a {@link #restoreOriginal()} method that can be used to restore the
   *         original EbeanServer implementation.
   */
  public static MockiEbean start(EbeanServer mock) {

    // using $mock as the server name
    EbeanServer original = Ebean.mock("$mock", mock, true);

    return new MockiEbean(mock, original);
  }

  protected final EbeanServer original;

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
