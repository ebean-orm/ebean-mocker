package org.example.domain;

import io.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product extends Model {

  public static final ProductFinder FIND = new ProductFinder();

  @Id 
  Long id;
  
  String name;

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
