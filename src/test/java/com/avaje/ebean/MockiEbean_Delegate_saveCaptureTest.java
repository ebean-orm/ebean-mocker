package com.avaje.ebean;

import org.assertj.core.api.Condition;
import org.example.domain.Customer;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MockiEbean_Delegate_saveCaptureTest extends BaseTest {

  @Test
  public void save_capture() throws Exception {

    final Customer foo = new Customer("foo");
    final Customer bar = new Customer("bar");
    final Customer baz = new Customer("baz");
    final Customer buz = new Customer("buz");
    final Customer boz = new Customer("boz");

    // by default persisting method calls such as
    // save(), delete() etc are not 'passed on' to
    // the underlying delegate EbeanServer

    DelegateEbeanServer mock = new DelegateEbeanServer();
    //mock.withPersisting(true);

    MockiEbean.runWithMock(mock, new Runnable() {

      @Override
      public void run() {

        // these are actually not saved to underlying db
        // but instead just captured in 'savedBeans'
        // save() -> capturedBeans.save
        foo.save();
        bar.save();
        baz.save();

        Ebean.save(buz);
        Ebean.getServer(null).save(boz);
      }
    });

    assertThat(mock.capturedBeans.save).contains(foo, bar, baz, buz, boz);
    assertThat(mock.methodCalls.save()).hasSize(5);
  }

  @Test
  public void insert_capture() throws Exception {

    final Customer foo = new Customer("foo");
    final Customer bar = new Customer("bar");
    final Customer baz = new Customer("baz");

    // by default persisting method calls such as
    // save(), delete() etc are not 'passed on' to
    // the underlying delegate EbeanServer

    DelegateEbeanServer mock = new DelegateEbeanServer();

    MockiEbean.runWithMock(mock, new Runnable() {

      @Override
      public void run() {

        // insert() -> capturedBeans.insert
        foo.insert();
        bar.insert();

        // save() -> capturedBeans.save
        baz.save();
      }
    });

    assertThat(mock.capturedBeans.insert).contains(foo, bar);
    assertThat(mock.capturedBeans.save).contains(baz);

    assertThat(mock.methodCalls.insert()).hasSize(2);
    assertThat(mock.methodCalls.save()).hasSize(1);


    assertThat(mock.methodCalls.size()).isEqualTo(3);
    assertThat(mock.methodCalls.all()).hasSize(3);
    assertThat(mock.methodCalls.update()).isEmpty();
    assertThat(mock.methodCalls.delete()).isEmpty();

  }

}