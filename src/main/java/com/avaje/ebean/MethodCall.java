package com.avaje.ebean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the method name and arguments.
 * <p/>
 * Used to collect the invoked methods for asserting.
 */
public class MethodCall {

  String name;

  Map<String,Object> args = new LinkedHashMap<String, Object>();

  public MethodCall(String name) {
    this.name = name;
  }

  public String toString() {
    return name+":"+args;
  }


  /**
   * Add the single argument.
   */
  public MethodCall with(String name1, Object arg1) {
    return with(name1, arg1, null, null, null, null);
  }

  /**
   * Add the two arguments.
   */
  public MethodCall with(String name1, Object arg1, String name2, Object arg2) {
    return with(name1, arg1, name2, arg2, null, null);
  }

  /**
   * Add three arguments.
   */
  public MethodCall with(String name1, Object arg1, String name2, Object arg2, String name3, Object arg3) {
    return with(name1, arg1, name2, arg2, name3, arg3, null, null);
  }

  /**
   * Add four arguments.
   */
  public MethodCall with(String name1, Object arg1, String name2, Object arg2, String name3, Object arg3, String name4, Object arg4) {
    args.put(name1, arg1);
    if (name2 != null) {
      args.put(name2, arg2);
      if (name3 != null) {
        args.put(name3, arg3);
        if (name4 != null) {
          args.put(name4, arg4);
        }
      }
    }
    return this;
  }

  public static MethodCall of(String methodName) {
    return new MethodCall(methodName);
  }
}
