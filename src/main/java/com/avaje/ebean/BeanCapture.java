package com.avaje.ebean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base functionality for capturing beans that are sent to the save(), delete() methods
 * of an EbeanServer.
 * <p/>
 * Capture the beans in order to use with test asserts.
 */
public abstract class BeanCapture {

  protected boolean captureBeans = true;

  /**
   * The captured beans sent to the save() methods.
   */
  public List<Object> savedBeans = new ArrayList<Object>();

  /**
   * The captured beans sent to the insert() methods.
   */
  public List<Object> insertedBeans = new ArrayList<Object>();

  /**
   * The captured beans sent to the update() methods.
   */
  public List<Object> updatedBeans = new ArrayList<Object>();

  /**
   * The captured beans sent to the delete() methods.
   * <p/>
   * Note that these can include MethodCall objects for the cases when
   * delete by id is called.
   */
  public List<Object> deletedBeans = new ArrayList<Object>();


  //-- Override these methods if necessary -------------

  protected void addSaved(Object bean) {
    if (captureBeans) {
      savedBeans.add(bean);
    }
  }

  protected void addSavedAll(Collection<?> beans) {
    if (captureBeans) {
      savedBeans.addAll(beans);
    }
  }

  protected void addInsertedAll(Collection<?> beans) {
    if (captureBeans) {
      insertedBeans.addAll(beans);
    }
  }

  protected void addInserted(Object bean) {
    if (captureBeans) {
      insertedBeans.add(bean);
    }
  }

  protected void addUpdatedAll(Collection<?> beans) {
    if (captureBeans) {
      updatedBeans.addAll(beans);
    }
  }

  protected void addUpdated(Object bean) {
    if (captureBeans) {
      updatedBeans.add(bean);
    }
  }

  protected void addDeletedAll(Collection<?> beans) {
    if (captureBeans) {
      deletedBeans.addAll(beans);
    }
  }

  protected void addDeleted(Object bean) {
    if (captureBeans) {
      deletedBeans.add(bean);
    }
  }

}
