package io.ebean;

/**
 * Created by rob on 17/07/15.
 */
public class FinderFieldNotFoundException extends RuntimeException {

  public FinderFieldNotFoundException(NoSuchFieldException e) {
    super(e);
  }
}
