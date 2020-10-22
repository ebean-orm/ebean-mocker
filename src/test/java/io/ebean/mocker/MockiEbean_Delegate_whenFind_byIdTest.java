package io.ebean.mocker;

import io.ebean.MockiEbean;
import org.example.domain.Customer;
import org.example.domain.Product;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MockiEbean_Delegate_whenFind_byIdTest extends BaseTest {

  final Customer bar = new Customer("bar");

  final Customer baz42 = new Customer("baz42");

  final Customer foo = new Customer("foo");

  @Test
  public void byId() {

    DelegateEbeanServer mock = new DelegateEbeanServer();

    mock.whenFind().byId(Customer.class).thenReturn(foo);

    MockiEbean.runWithMock(mock, () -> {

      // Note: id value does not need to match here
      Customer found = Customer.find.byId(123L);
      assertThat(found).isSameAs(foo);
    });
  }













  @Test
  public void byId_withValue() throws Exception {

    DelegateEbeanServer mock = new DelegateEbeanServer();

    // with specific id value
    mock.whenFind().byId(Customer.class, 42L).thenReturn(baz42);

    // no id value, more of a 'fallback' response
    mock.whenFind().byId(Customer.class).thenReturn(bar);


    MockiEbean.runWithMock(mock, new Runnable() {

      @Override
      public void run() {

        // specific (id value match)
        Customer found = Customer.find.byId(42L);
        assertThat(found).isSameAs(baz42);

        // general / 'fallback' response
        found = Customer.find.byId(98L);
        assertThat(found).isSameAs(bar);

        // general / 'fallback' response
        found = Customer.find.byId(123L);
        assertThat(found).isSameAs(bar);


        // No whenFind for MyBean so this actually
        // calls through to underlying EbeanServer
        Product.FIND.byId(23L);

      }
    });
  }


}