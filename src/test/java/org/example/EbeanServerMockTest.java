package org.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.MockiEbean;
import io.ebeaninternal.server.core.DefaultServer;

public class EbeanServerMockTest {

  

  @Test
  public void testWithoutInitial() {

    // Setup
    Long magicBeanId = Long.valueOf(47L);
    EbeanServer mock = Mockito.mock(EbeanServer.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    // Start mock Ebean session
    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      EbeanServer server = Ebean.getServer(null);
      Object beanId = server.getBeanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      // restore Ebean default server
      mockiEbean.restoreOriginal();
    }

    EbeanServer restoredServer = Ebean.getServer(null);
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }
  
  @Test
  public void testWithMockito() {

    EbeanServer defaultServer = Ebean.getServer(null);
    assertNotNull("server starts", defaultServer);
    assertTrue("is a real EbeanServer", defaultServer instanceof DefaultServer);

    Long magicBeanId = Long.valueOf(47L);

    EbeanServer mock = Mockito.mock(EbeanServer.class);
    when(mock.getBeanId(null)).thenReturn(magicBeanId);

    MockiEbean mockiEbean = MockiEbean.start(mock);
    try {

      // So using the Ebean singleton returns the mock instance
      EbeanServer server = Ebean.getServer(null);
      Object beanId = server.getBeanId(null);

      assertEquals(magicBeanId, beanId);

    } finally {
      mockiEbean.restoreOriginal();
    }

    EbeanServer restoredServer = Ebean.getServer(null);
    assertTrue("is a real EbeanServer", restoredServer instanceof DefaultServer);
  }

}
