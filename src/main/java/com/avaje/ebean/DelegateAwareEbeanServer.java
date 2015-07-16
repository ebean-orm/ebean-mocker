package com.avaje.ebean;

/**
 * Created by rob on 16/07/15.
 */
public interface DelegateAwareEbeanServer {

  boolean withDelegateIfRequired(EbeanServer delegate);

}
