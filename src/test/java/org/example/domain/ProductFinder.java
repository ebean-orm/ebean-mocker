package org.example.domain;

import com.avaje.ebean.Model;


public class ProductFinder extends Model.Finder<Long,Product> {

  public ProductFinder() {
    super(Product.class);
  }


}
