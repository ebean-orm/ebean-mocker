package org.example.domain;

import io.ebean.Finder;


public class ProductFinder extends Finder<Long,Product> {

  public ProductFinder() {
    super(Product.class);
  }


}
