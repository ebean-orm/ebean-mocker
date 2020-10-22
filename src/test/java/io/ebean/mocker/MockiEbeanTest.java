package io.ebean.mocker;

import io.ebean.DB;
import io.ebean.Database;
import io.ebeaninternal.server.core.DefaultServer;
import io.ebean.MockiEbean;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MockiEbeanTest {

  @Test
  public void testRunWithMock() {

    // Setup
    final Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    MockiEbean.runWithMock(mock, () -> {
      Object value = DB.getBeanId(null);
      assertEquals(value, magicBeanId);
    });

    // assert that the original EbeanServer has been restored
    Database restoredServer = DB.getDefault();
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

  @Test(expected = IllegalStateException.class)
  public void testRunWithMock_when_exceptionThrow_should_restoreServer() {

    // Setup with Mockito
    final Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    try {
      MockiEbean.runWithMock(mock, (Runnable) () -> {
        Object value = DB.getBeanId(null);
        assertEquals(value, magicBeanId);
        throw new IllegalStateException();
      });

    } finally {
      // assert that the original EbeanServer has been restored
      Database restoredServer = DB.getDefault();
      assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
    }
  }

  @Test
  public void testRunCallableWithMock() throws Exception {

    // Using a test double rather than a Mockito mock

    long result =
      MockiEbean.runWithMock(new TDMockServer(), () -> {
        Object value = DB.getBeanId(new Object());
        assertEquals(107L, value);
        return 142L;
      });

    assertEquals(142L, result);

    // assert that the original EbeanServer has been restored
    Database restoredServer = DB.getDefault();
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

  @Test(expected = IllegalStateException.class)
  public void testRunCallableWithMock_when_exceptionThrow_should_restoreServer() throws Exception {

    // Using a test double rather than a Mockito mock

    try {
      long result =
        MockiEbean.runWithMock(new TDMockServer(), () -> {
          Object value = DB.getBeanId(new Object());

          assertEquals(107L, value);
          throw new IllegalStateException();
        });

    } finally {
      // assert that the original EbeanServer has been restored
      Database restoredServer = DB.getDefault();
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
