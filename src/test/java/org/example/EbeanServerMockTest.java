package org.example;

import io.ebean.DB;
import io.ebean.Database;
import io.ebean.MockiEbean;
import io.ebeaninternal.server.core.DefaultServer;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class EbeanServerMockTest {

  @Test
  public void testWithoutInitial() {

    // Setup
    Long magicBeanId = Long.valueOf(47L);
    Database mock = Mockito.mock(Database.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    // Start mock Ebean session
    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      Database server = DB.getDefault();
      Object beanId = server.getBeanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      // restore Ebean default server
      mockiEbean.restoreOriginal();
    }

    Database restoredServer = DB.getDefault();
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

  @Test
  public void testWithMockito() {

    Database defaultServer = DB.getDefault();
    assertNotNull("server starts", defaultServer);
    assertTrue("is a real EbeanServer", defaultServer instanceof DefaultServer);

    Long magicBeanId = Long.valueOf(47L);

    Database mock = Mockito.mock(Database.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      Database server = DB.getDefault();
      Object beanId = server.getBeanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      mockiEbean.restoreOriginal();
    }

    Database restoredServer = DB.getDefault();
    assertTrue("is a real Database", restoredServer instanceof DefaultServer);
  }

}
