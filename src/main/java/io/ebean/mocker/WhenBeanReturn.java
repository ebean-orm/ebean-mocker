package io.ebean.mocker;

/**
 * Created by rob on 16/07/15.
 */
public class WhenBeanReturn<T> {

  Class<T> beanType;

  Object id;

  T bean;

  public WhenBeanReturn(Class<T> beanType) {
    this(beanType, null);
  }

  public WhenBeanReturn(Class<T> beanType, Object id) {
    this.beanType = beanType;
    this.id = id;
  }

  public void thenReturn(T bean) {
    this.bean = bean;
  }

  T val() {
    return bean;
  }

  /**
   * Return true if matched by beanType and there is no specific id set.
   */
  boolean isMatch(Class<?> beanType) {
    return beanType.equals(this.beanType) && this.id == null;
  }

  /**
   * Return true if matched by beanType and id value.
   */
  boolean isMatch(Class<?> beanType, Object id) {

    return beanType.equals(this.beanType) && idMatch(id);
  }

  boolean idMatch(Object id) {
    if (id == null) return this.id == null;
    return id.equals(this.id);
  }
}
