package io.ebean;

/**
 * Created by rob on 17/07/15.
 */
public class FinderIllegalAccessException extends RuntimeException {

  public FinderIllegalAccessException(IllegalAccessException e) {
    super(e);
  }
}
