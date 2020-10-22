package io.ebean.mocker;

/**
 * Created by rob on 17/07/15.
 */
public class FinderFieldNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FinderFieldNotFoundException(NoSuchFieldException e) {
    super(e);
  }
}
