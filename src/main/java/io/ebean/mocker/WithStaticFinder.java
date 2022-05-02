package io.ebean.mocker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Used to replace a "Finder" that is located as a static field (typically on an Model entity bean).
 * <p/>
 * This uses reflection to get/set the finder implementation with the ability to replace the original implementation
 * with the test double and restoring the original.
 */
public class WithStaticFinder<T> {

  final Class<T> beanType;

  String fieldName;

  Field field;

  Object original;

  Object testDouble;

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
  public WithStaticFinder<T> as(Object testDouble) throws FinderFieldNotFoundException {
    try {
      this.testDouble = testDouble;
      this.field = findField();
      this.field.setAccessible(true);
      try {
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        /**
         * If the project using this library has a SecurityManager set up, permission may be denied.
         * Therefor, running this as a privileged action.
         */
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
          modifiersField.setAccessible(true);
          return null;
        });
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
      } catch (NoSuchFieldException e) {
        // this fails with Java 17
        throw new RuntimeException("Unable to turn off final field flag for " + field, e);
      }

      this.original = field.get(null);
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

  /**
   * Set the given test double onto the given class returning the WithStaticFinder.
   * <p>
   * Use restore to put the original finder back onto the class.
   * </p>
   *
   * <pre>{@code
   *
   *   // Replace the finder with TDCustFinder
   *   WithStaticFinder with = WithStaticFinder.use(Customer.class, new TDCustFinder());
   *   try {
   *     // perform some test
   *     Customer jim = Customer.find.byName("jim");
   *
   *   } finally {
   *     // restore the original finder
   *     with.restoreOriginal();
   *   }
   *
   * }</pre>
   */
  public static <U> WithStaticFinder<U> use(Class<U> cls, Object testFinder) {

    WithStaticFinder<U> with = new WithStaticFinder<>(cls);
    with.as(testFinder);
    with.useTestDouble();
    return with;
  }
}
