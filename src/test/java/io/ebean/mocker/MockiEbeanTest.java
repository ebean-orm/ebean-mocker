package io.ebean.mocker;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.MockiEbean;
import io.ebeaninternal.server.core.DefaultServer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class MockiEbeanTest {

  @Test
  public void testRunWithMock() {

    // Setup
    final Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.beanId(null)).thenReturn(magicBeanId);

    MockiEbean.runWithMock(mock, () -> {
      Object value = DB.beanId(null);
      assertThat(value).isEqualTo(magicBeanId);
    });

    // assert that the original EbeanServer has been restored
    Database restoredServer = DB.getDefault();
    assertThat(restoredServer).isInstanceOf(DefaultServer.class);
  }

  @Test
  public void testRunWithMock_when_exceptionThrow_should_restoreServer() {

    // Setup with Mockito
    final Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.beanId(null)).thenReturn(magicBeanId);
    assertThatThrownBy(() -> {
      try {
        MockiEbean.runWithMock(mock, (Runnable) () -> {
          Object value = DB.beanId(null);
          assertThat(value).isEqualTo(magicBeanId);
          throw new IllegalStateException();
        });

      } finally {
        // assert that the original EbeanServer has been restored
        Database restoredServer = DB.getDefault();
        assertThat(restoredServer).isInstanceOf(DefaultServer.class);
      }
    }).isInstanceOf(IllegalStateException.class);
  }

  @Test
  public void testRunCallableWithMock() throws Exception {

    // Using a test double rather than a Mockito mock

    long result =
      MockiEbean.runWithMock(new TDMockServer(), () -> {
        Object value = DB.beanId(new Object());
        assertThat(value).isEqualTo(107L);
        return 142L;
      });

    assertThat(result).isEqualTo(142L);

    // assert that the original EbeanServer has been restored
    Database restoredServer = DB.getDefault();
    assertThat(restoredServer).isInstanceOf(DefaultServer.class);
  }

  @Test
  public void testRunCallableWithMock_when_exceptionThrow_should_restoreServer() throws Exception {
    // Using a test double rather than a Mockito mock
    assertThatThrownBy(() -> {
      try {
        long result =
                MockiEbean.runWithMock(new TDMockServer(), () -> {
                  Object value = DB.beanId(new Object());

                  assertThat(value).isEqualTo(107L);
                  throw new IllegalStateException();
                });

      } finally {
        // assert that the original EbeanServer has been restored
        Database restoredServer = DB.getDefault();
        assertThat(restoredServer).isInstanceOf(DefaultServer.class);
      }
    }).isInstanceOf(IllegalStateException.class);
  }

  /**
   * Test double EbeanServer.
   */
  class TDMockServer extends TDDatabase {

    @Override
    public Object beanId(Object bean) {
      return 107L;
    }
  }
}
