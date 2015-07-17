package com.avaje.ebean;

import org.avaje.ebeantest.LoggedSql;
import org.example.domain.Customer;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class MockiEbean_Delegate_persistingTest extends BaseTest {

  @Test
  public void save_withPersistingTrue() throws Exception {

    final Customer foo = new Customer("foo");
    final Customer bar = new Customer("bar");
    final Customer baz = new Customer("baz");

    // by default persisting method calls such as
    // save(), delete() etc are not 'passed on' to
    // the underlying delegate EbeanServer

    DelegateEbeanServer mock = new DelegateEbeanServer();

    // set true so save() etc passed onto underlying delegate
    // which is means actual saves into our H2 test db
    mock.withPersisting(true);

    LoggedSql.start();

    MockiEbean.runWithMock(mock, new Runnable() {

      @Override
      public void run() {

        // the beans saved are 'captured' by DelegateEbeanServer by default
        // and with persisting true these calls passed onto the underlying EbeanServer
        foo.save();
        bar.save();
        baz.save();

        Customer foundFoo = Customer.find.byId(foo.getId());

        assertThat(foundFoo.getId()).isSameAs(foo.getId());
        assertThat(foundFoo.getName()).isSameAs(foo.getName());
      }
    });


    assertThat(mock.capturedBeans.save).contains(foo, bar, baz);
    assertThat(mock.methodCalls.save()).hasSize(3);

    // in the form of: txn[1003] insert into customer (id, name) values (?,?); --bind(1,foo,)
    List<String> sqlLogs = LoggedSql.stop();

    for (int i = 0; i < 3; i++) {
      assertThat(sqlLogs.get(i)).contains("insert into customer (id, name) values (?,?)");
    }

    assertThat(sqlLogs.get(3)).contains("select t0.id c0, t0.name c1 from customer t0 where t0.id = ?");
  }

}