package io.ebean.mocker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rob on 17/07/15.
 */
public class MethodCalls {

  List<MethodCall> list = new ArrayList<>();

  public void add(MethodCall call) {
    list.add(call);
  }

  public int size() {
    return list.size();
  }

  public List<MethodCall> all() {
    return list;
  }

  public List<MethodCall> save() {
    return matches(DelegateMethodNames.SAVE);
  }

  public List<MethodCall> insert() {
    return matches(DelegateMethodNames.INSERT);
  }

  public List<MethodCall> update() {
    return matches(DelegateMethodNames.UPDATE);
  }

  public List<MethodCall> delete() {
    return matches(DelegateMethodNames.DELETE);
  }

  protected List<MethodCall> matches(String methodName) {

    List<MethodCall> matches = new ArrayList<>();
    for (MethodCall call : list) {
      if (isMatch(call, methodName)) {
        matches.add(call);
      }
    }
    return matches;
  }

  protected static boolean isMatch(MethodCall call, String methodName) {
    return call.name.equals(methodName);
  }
}
