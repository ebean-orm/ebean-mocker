package org.example.domain;

import io.ebean.Finder;

import java.util.List;

public class CustomerFinder extends Finder<Long,Customer> {

  public CustomerFinder() {
    super(Customer.class);
  }

  public Customer byUniqueName(String name) {

    return query().where().eq("name", name).findOne();
  }

  public List<Customer> byName(String name) {

    return query().where().eq("name", name).findList();
  }

}
