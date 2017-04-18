package io.ebean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base functionality for capturing beans that are sent to the save(), delete() methods
 * of an EbeanServer.
 * <p/>
 * Capture the beans in order to use with test asserts.
 */
public class BeanCapture {

  protected boolean captureBeans = true;

  /**
   * The captured beans sent to the save() methods.
   */
  public List<Object> save = new ArrayList<>();

  /**
   * The captured beans sent to the insert() methods.
   */
  public List<Object> insert = new ArrayList<>();

  /**
   * The captured beans sent to the update() methods.
   */
  public List<Object> update = new ArrayList<>();

  /**
   * The captured beans sent to the delete() methods.
   * <p/>
   * Note that these can include MethodCall objects for the cases when
   * delete by id is called.
   */
  public List<Object> delete = new ArrayList<>();

  /**
   * Captured beans sent to deletePermanent() methods.
   */
  public List<Object> deletePermanent = new ArrayList<>();


  protected void addSaved(Object bean) {
    if (captureBeans) {
      save.add(bean);
    }
  }

  protected void addSavedAll(Collection<?> beans) {
    if (captureBeans) {
      save.addAll(beans);
    }
  }

  protected void addInsertedAll(Collection<?> beans) {
    if (captureBeans) {
      insert.addAll(beans);
    }
  }

  protected void addInserted(Object bean) {
    if (captureBeans) {
      insert.add(bean);
    }
  }

  protected void addUpdatedAll(Collection<?> beans) {
    if (captureBeans) {
      update.addAll(beans);
    }
  }

  protected void addUpdated(Object bean) {
    if (captureBeans) {
      update.add(bean);
    }
  }

  protected void addDeletedAll(Collection<?> beans) {
    if (captureBeans) {
      delete.addAll(beans);
    }
  }

  protected void addDeleted(Object bean) {
    if (captureBeans) {
      delete.add(bean);
    }
  }

  protected void addDeletePermanent(Object bean) {
    if (captureBeans) {
      deletePermanent.add(bean);
    }
  }

  public void addDeletedAllPermanent(Collection<?> beans) {
    if (captureBeans) {
      deletePermanent.addAll(beans);
    }
  }
}
