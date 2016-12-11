package io.ebean.delegate;

import io.ebean.EbeanServer;
import io.ebean.ExpressionFactory;
import io.ebean.Query;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.querydefn.DefaultOrmQuery;

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

