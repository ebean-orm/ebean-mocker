package com.avaje.ebean;

import org.example.domain.Customer;
import org.example.domain.MyBean;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class WhenFindTest {


  Customer foo = new Customer(78L, "foo");

  Customer bar = new Customer(78L, "bar");

  @Test
  public void testById_idValueSpecified() throws Exception {

    WhenFind whenFind = new WhenFind();
    whenFind.byId(Customer.class, 42L).thenReturn(foo);

    WhenBeanReturn match = whenFind.findMatchById(Customer.class, 42L);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(Customer.class, 43L);
    assertThat(match).isNull();

    match = whenFind.findMatchById(MyBean.class, 42L);
    assertThat(match).isNull();

    match = whenFind.findMatchById(Customer.class, null);
    assertThat(match).isNull();

  }

  @Test
  public void testById_noIdValue() throws Exception {

    WhenFind whenFind = new WhenFind();
    whenFind.byId(Customer.class).thenReturn(foo);

    WhenBeanReturn match = whenFind.findMatchById(Customer.class, 42L);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(Customer.class, 43L);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(Customer.class, null);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(MyBean.class, 42L);
    assertThat(match).isNull();

  }


  @Test
  public void testById_matchMostSpecific() throws Exception {

    WhenFind whenFind = new WhenFind();
    whenFind.byId(Customer.class).thenReturn(foo);//general
    whenFind.byId(Customer.class, 42L).thenReturn(bar);//specific

    WhenBeanReturn match = whenFind.findMatchById(Customer.class, 42L);
    assertThat(match.val()).isSameAs(bar);

    match = whenFind.findMatchById(Customer.class, 43L);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(Customer.class, null);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchById(MyBean.class, 42L);
    assertThat(match).isNull();

  }

  @Test
  public void testUnique() {

    WhenFind whenFind = new WhenFind();
    whenFind.byUnique(Customer.class).thenReturn(foo);

    WhenBeanReturn match = whenFind.findMatchByUnique(Customer.class);
    assertThat(match.val()).isSameAs(foo);

    match = whenFind.findMatchByUnique(MyBean.class);
    assertThat(match).isNull();

  }

}