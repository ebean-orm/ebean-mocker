package com.avaje.ebean;

import com.avaje.ebeaninternal.server.core.DefaultServer;
import org.avaje.agentloader.AgentLoader;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MockiEbeanTest {

  protected static Logger logger = LoggerFactory.getLogger(MockiEbeanTest.class);

  static {
    logger.debug("... preStart");
    if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages=org.example.**")) {
      logger.info("avaje-ebeanorm-agent not found in classpath - not dynamically loaded");
    }
  }

  @Test
  public void testRunWithMock() throws Exception {

    // Setup
    final Long magicBeanId = Long.valueOf(47L);
    EbeanServer mock = Mockito.mock(EbeanServer.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    MockiEbean.runWithMock(mock, new Runnable() {
      @Override
      public void run() {
        Object value = Ebean.getServer(null).getBeanId(null);

        assertEquals(magicBeanId, value);
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
      MockiEbean.runWithMock(mock, new Runnable() {
        @Override
        public void run() {
          Object value = Ebean.getServer(null).getBeanId(null);

          assertEquals(magicBeanId, value);
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
      MockiEbean.runWithMock(new TDMockServer(), new Callable<Long>() {
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
          MockiEbean.runWithMock(new TDMockServer(), new Callable<Long>() {
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