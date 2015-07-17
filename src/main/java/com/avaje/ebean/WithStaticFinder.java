package com.avaje.ebean;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Used to replace a "Finder" that is located as a static field (typically on an Model entity bean).
 * <p/>
 * This uses reflection to get/set the finder implementation with the ability to replace the original implementation
 * with the test double and restoring the original.
 */
public class WithStaticFinder<T> {

  Class<T> beanType;

  String fieldName;

  Field field;

  Model.Find<?, T> original;

  Model.Find<?, T> testDouble;

  /**
   * Construct with a given bean type.
   */
  public WithStaticFinder(Class<T> beanType) {
    this.beanType = beanType;
  }

  /**
   * Construct with a given bean type.
   */
  public WithStaticFinder(Class<T> beanType, String fieldName) {
    this.beanType = beanType;
    this.fieldName = fieldName;
  }

  /**
   * Set the test double instance to use after useTestDouble() has been called.
   * <p/>
   * Note that the test double instance is not set until <code>useTestDouble()</code> is called.
   */
  public WithStaticFinder as(Model.Find<?, T> testDouble) throws FinderFieldNotFoundException {

    try {
      this.testDouble = testDouble;

      this.field = findField();
      this.field.setAccessible(true);
      try {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
      }

      this.original = (Model.Find<?, T>) field.get(null);
      return this;

    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * Set the test double to the field using reflection.
   * <p/>
   * After this the test double will be used by calling code.
   */
  public void useTestDouble() {
    try {
      this.field.set(null, testDouble);
    } catch (IllegalAccessException e) {
      throw new FinderIllegalAccessException(e);
    }
  }

  /**
   * Restore the original implementation using reflection.
   */
  public void restoreOriginal() {

    try {
      this.field.set(null, original);
    } catch (IllegalAccessException e) {
      throw new FinderIllegalAccessException(e);
    }
  }

  /**
   * Find and return the "find" field.
   */
  protected Field findField() throws FinderFieldNotFoundException {

    try {
      if (fieldName != null) {
        return beanType.getField(fieldName);
      }

      try {
        return beanType.getField("find");

      } catch (NoSuchFieldException e) {
        return beanType.getField("FIND");
      }
    } catch (NoSuchFieldException e) {
      throw new FinderFieldNotFoundException(e);
    }
  }


}
