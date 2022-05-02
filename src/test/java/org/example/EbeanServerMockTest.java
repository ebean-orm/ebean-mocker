package org.example;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.MockiEbean;
import io.ebeaninternal.server.core.DefaultServer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class EbeanServerMockTest {

  @Test
  public void testWithoutInitial() {

    // Setup
    Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.beanId(null)).thenReturn(magicBeanId);

    // Start mock Ebean session
    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      Database server = DB.getDefault();
      Object beanId = server.beanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      // restore Ebean default server
      mockiEbean.restoreOriginal();
    }

    Database restoredServer = DB.getDefault();
    assertThat(restoredServer).isInstanceOf(DefaultServer.class);
  }

  @Test
  public void testWithMockito() {

    Database defaultServer = DB.getDefault();
    assertNotNull(defaultServer);
    assertThat(defaultServer).isInstanceOf(DefaultServer.class);

    Long magicBeanId = Long.valueOf(47L);

    Database mock = Mockito.mock(Database.class);
    when(mock.beanId(null)).thenReturn(magicBeanId);

    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      Database server = DB.getDefault();
      Object beanId = server.beanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      mockiEbean.restoreOriginal();
    }

    Database restoredServer = DB.getDefault();
    assertThat(restoredServer).isInstanceOf(DefaultServer.class);
  }

}
