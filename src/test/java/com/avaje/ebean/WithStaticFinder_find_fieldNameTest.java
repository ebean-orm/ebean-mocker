package com.avaje.ebean;

import org.example.domain.Customer;
import org.example.domain.CustomerFinder;
import org.example.domain.MyBean;
import org.example.domain.MyBeanFinder;
import org.example.domain.Product;
import org.example.domain.ProductFinder;
import org.junit.Test;

import javax.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;


public class WithStaticFinder_find_fieldNameTest {


  @Test
  public void testAsUseTestDoubleRestoreOriginal() {

    TDCustomerFinder testDouble = new TDCustomerFinder();

    WithStaticFinder staticFinder = new WithStaticFinder(Customer.class);
    staticFinder.as(testDouble);

    Object original = staticFinder.original;
    assertThat(Customer.find).isSameAs(original);

    staticFinder.useTestDouble();

    assertThat(Customer.find).isSameAs(testDouble);
    assertThat(Customer.find).isNotSameAs(original);

    Customer found = Customer.find.byId(89L);
    assertThat(found).isSameAs(dummyFoo);

    staticFinder.restoreOriginal();

    assertThat(Customer.find).isSameAs(original);
    assertThat(Customer.find).isNotSameAs(testDouble);

  }

  @Test
  public void testUseStatic() {

    TDCustomerFinder testDouble = new TDCustomerFinder();
    assertThat(Customer.find).isNotSameAs(testDouble);

    WithStaticFinder<Customer> with = WithStaticFinder.use(Customer.class, testDouble);
    assertThat(Customer.find).isSameAs(testDouble);

    Customer byId = Customer.find.byId(42L);
    assertThat(byId).isSameAs(dummyFoo);

    with.restoreOriginal();
    assertThat(Customer.find).isNotSameAs(testDouble);
  }

  Customer dummyFoo = new Customer();

  class TDCustomerFinder extends CustomerFinder {
    @Override
    public Customer byId(Long id) {
      return dummyFoo;
    }
  }
}