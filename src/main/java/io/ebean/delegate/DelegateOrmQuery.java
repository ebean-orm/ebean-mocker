package io.ebean.delegate;

import io.ebean.Database;
import io.ebean.ExpressionFactory;
import io.ebean.Query;
import io.ebeaninternal.api.SpiEbeanServer;
import io.ebeaninternal.server.deploy.BeanDescriptor;
import io.ebeaninternal.server.querydefn.DefaultOrmQuery;

public class DelegateOrmQuery<T> extends DefaultOrmQuery<T> {

  public static <T> DelegateOrmQuery<T> copy(Query<T> source, Database delegateServer) {
    return copy((DefaultOrmQuery<T>) source, delegateServer);
  }

  public static <T> DelegateOrmQuery<T> copy(DefaultOrmQuery<T> source, Database delegateServer) {
    return new DelegateOrmQuery<T>(source.getBeanDescriptor(), delegateServer, source.getExpressionFactory());
  }

  public DelegateOrmQuery(BeanDescriptor<T> beanType, Database server, ExpressionFactory expressionFactory) {
    super(beanType, (SpiEbeanServer) server, expressionFactory);
  }

}

