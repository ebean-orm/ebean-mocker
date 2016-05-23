package com.avaje.ebean.delegate;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.ExpressionFactory;
import com.avaje.ebean.Query;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.querydefn.DefaultOrmQuery;

/**
 */
public class DelegateOrmQuery<T> extends DefaultOrmQuery<T> {

  public static <T> DelegateOrmQuery<T> copy(Query<T> source, EbeanServer delegateServer) {

    return copy((DefaultOrmQuery<T>)source, delegateServer);
  }

  public static <T> DelegateOrmQuery<T> copy(DefaultOrmQuery<T> source, EbeanServer delegateServer) {

    return new DelegateOrmQuery<>(source.getBeanDescriptor(), delegateServer, source.getExpressionFactory());
  }

  public DelegateOrmQuery(BeanDescriptor<T> beanType, EbeanServer server, ExpressionFactory expressionFactory) {
    super(beanType, server, expressionFactory);
  }

}

