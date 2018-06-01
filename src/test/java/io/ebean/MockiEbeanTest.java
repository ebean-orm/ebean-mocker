package io.ebean;

import io.ebeaninternal.server.core.DefaultServer;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MockiEbeanTest {

  @Test
  public void testRunWithMock() throws Exception {

    // Setup
    final Long magicBeanId = Long.valueOf(47L);
    EbeanServer mock = Mockito.mock(EbeanServer.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    io.ebean.MockiEbean.runWithMock(mock, new Runnable() {
      @Override
      public void run() {
        Object value = Ebean.getServer(null).getBeanId(null);

        assertEquals(value, magicBeanId);
      }
    });

    // assert that the original EbeanServer has been restored
    EbeanServer restoredServer = Ebean.getServer(null);
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

  @Test(expected = IllegalStateException.class)
  public void testRunWithMock_when_exceptionThrow_should_restoreServer() throws Exception {

    // Setup with Mockito
    final Long magicBeanId = Long.valueOf(47L);
    EbeanServer mock = Mockito.mock(EbeanServer.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    try {
      io.ebean.MockiEbean.runWithMock(mock, new Runnable() {
        @Override
        public void run() {
          Object value = Ebean.getServer(null).getBeanId(null);

          assertEquals(value, magicBeanId);
          throw new IllegalStateException();
        }
      });

    } finally {
      // assert that the original EbeanServer has been restored
      EbeanServer restoredServer = Ebean.getServer(null);
      assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
    }
  }

  @Test
  public void testRunCallableWithMock() throws Exception {

    // Using a test double rather than a Mockito mock

    long result =
      io.ebean.MockiEbean.runWithMock(new TDMockServer(), new Callable<Long>() {
        @Override
        public Long call() {
          Object value = Ebean.getServer(null).getBeanId(new Object());

          assertEquals(107L, value);
          return 142L;
        }
      });

    assertEquals(142L, result);

    // assert that the original EbeanServer has been restored
    EbeanServer restoredServer = Ebean.getServer(null);
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

  @Test(expected = IllegalStateException.class)
  public void testRunCallableWithMock_when_exceptionThrow_should_restoreServer() throws Exception {

    // Using a test double rather than a Mockito mock

    try {
      long result =
        io.ebean.MockiEbean.runWithMock(new TDMockServer(), new Callable<Long>() {
          @Override
          public Long call() {
            Object value = Ebean.getServer(null).getBeanId(new Object());

            assertEquals(107L, value);
            throw new IllegalStateException();
          }
        });

    } finally {
      // assert that the original EbeanServer has been restored
      EbeanServer restoredServer = Ebean.getServer(null);
      assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
    }
  }

  /**
   * Test double EbeanServer.
   */
  class TDMockServer extends TDEbeanServer {

    @Override
    public Object getBeanId(Object bean) {
      return 107L;
    }
  }
}
