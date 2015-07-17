package org.example.domain;

import com.avaje.ebean.Model;

import java.util.List;

/**
 * Created by rob on 17/07/15.
 */
public class CustomerFinder extends Model.Finder<Long,Customer> {

  public CustomerFinder() {
    super(Customer.class);
  }

  public Customer byUniqueName(String name) {

    return query().where().eq("name", name).findUnique();
  }

  public List<Customer> byName(String name) {

    return query().where().eq("name", name).findList();
  }

}
