package org.example.domain;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer extends Model {

  public static final CustomerFinder find = new CustomerFinder();

  @Id
  Long id;

  String name;

  public Customer() {
  }

  public Customer(String name) {
    this.name = name;
  }

  public Customer(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public String toString() {
    return "<Customer id:"+id+" name:"+name+">";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
