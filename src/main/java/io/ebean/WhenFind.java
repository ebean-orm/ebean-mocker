package io.ebean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 16/07/15.
 */
public class WhenFind {

  List<WhenBeanReturn<?>> byId = new ArrayList<>();

  Map<Class<?>, WhenBeanReturn<?>> byUnique = new HashMap<>();

  public <T> WhenBeanReturn<T> byId(Class<T> beanType) {

    return byId(beanType, null);
  }

  public <T> WhenBeanReturn<T> byId(Class<T> beanType, Object id) {

    WhenBeanReturn<T> ret = new WhenBeanReturn<>(beanType, id);
    byId.add(ret);
    return ret;
  }

  public <T> WhenBeanReturn<T> byUnique(Class<T> beanType) {

    WhenBeanReturn<T> ret = new WhenBeanReturn<>(beanType);
    byUnique.put(beanType, ret);
    return ret;
  }

  protected WhenBeanReturn findMatchByUnique(Class<?> beanType) {
    return byUnique.get(beanType);
  }

  protected WhenBeanReturn findMatchById(Class<?> beanType, Object id) {

    for (WhenBeanReturn<?> byIdReturn : byId) {
      if (byIdReturn.isMatch(beanType, id)){
        return byIdReturn;
      }
    }

    for (WhenBeanReturn<?> byIdReturn : byId) {
      if (byIdReturn.isMatch(beanType)){
        return byIdReturn;
      }
    }

    // no match
    return null;
  }
}
