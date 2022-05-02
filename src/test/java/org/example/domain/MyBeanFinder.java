package org.example.domain;

import io.ebean.Finder;


public class MyBeanFinder extends Finder<Long, MyBean> {

  public MyBeanFinder() {
    super(MyBean.class);
  }


}
