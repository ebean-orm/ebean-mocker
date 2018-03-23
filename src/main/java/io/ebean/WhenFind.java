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

  @SuppressWarnings("unchecked")
  protected <T> WhenBeanReturn<T> findMatchByUnique(Class<T> beanType) {
    return (WhenBeanReturn<T>) byUnique.get(beanType);
  }

  @SuppressWarnings("unchecked")
  protected <T> WhenBeanReturn<T> findMatchById(Class<T> beanType, Object id) {

    for (WhenBeanReturn<?> byIdReturn : byId) {
      if (byIdReturn.isMatch(beanType, id)){
        return (WhenBeanReturn<T>) byIdReturn;
      }
    }

    for (WhenBeanReturn<?> byIdReturn : byId) {
      if (byIdReturn.isMatch(beanType)){
        return (WhenBeanReturn<T>) byIdReturn;
      }
    }

    // no match
    return null;
  }
}
