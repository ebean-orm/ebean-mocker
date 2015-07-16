package com.avaje.ebean;

import org.example.domain.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MockiEbeanDelegateEbeanServerTest extends BaseTest {

  @Test
  public void testSetProxyToIfRequired() throws Exception {

    final Customer foo = new Customer(42L, "foo");
    final Customer bar = new Customer(98L, "bar");
    final Customer baz = new Customer(99L, "baz");

    final DelegateEbeanServer mock = new DelegateEbeanServer();

    //mock.withPersisting(true);
    //mock.withDelegate(Ebean.getServer(null));

    mock.whenFind().byId(Customer.class, 42L).thenReturn(foo);
    //mock.whenFind().byId(Customer.class).thenReturn(bar);
    //mock.whenFind().byUnique(Customer.class).thenReturn(baz);

    MockiEbean.runWithMock(mock, new Runnable() {

      @Override
      public void run() {

        assertThat(Customer.find.byId(42L)).isSameAs(foo);
        //assertThat(Customer.find.byId(52L)).isSameAs(bar);
        //assertThat(Customer.find.where().findUnique()).isSameAs(baz);

      }
    });

  }
}