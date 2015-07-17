package org.example.domain;

import com.avaje.ebean.Model;

import java.util.List;


public class MyBeanFinder extends Model.Finder<Long,MyBean> {

  public MyBeanFinder() {
    super(MyBean.class);
  }


}
