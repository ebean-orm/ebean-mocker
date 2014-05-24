package com.avaje.ebean;

public class MockiEbean {

  public static MockiEbean start(EbeanServer mock) {
    
    EbeanServer original = Ebean.mock("$mock", mock, true);
    
    return new MockiEbean(mock, original);
  }

  
  final EbeanServer original;
  
  final EbeanServer mock;
  
  protected MockiEbean(EbeanServer mock, EbeanServer original) {
    this.mock = mock;
    this.original = original;
  }

  public EbeanServer getOriginal() {
    return original;
  }

  public EbeanServer getMock() {
    return mock;
  }
  
  public void restoreOriginal() {
    if (original == null) {
      throw new IllegalStateException("Original EbeanServer instance is null");
    }
    if (original.getName() == null) {
      throw new IllegalStateException("Original EbeanServer name is null");
    }
    
    // restore the original EbeanServer back
    Ebean.mock(original.getName(), original, true);
  }
  
}
