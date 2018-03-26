package io.ebean;

/**
 * Created by rob on 17/07/15.
 */
public class FinderIllegalAccessException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FinderIllegalAccessException(IllegalAccessException e) {
    super(e);
  }
}
