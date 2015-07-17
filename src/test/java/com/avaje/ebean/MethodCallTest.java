package com.avaje.ebean;

import org.junit.Test;

import javax.persistence.CascadeType;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodCallTest {

  @Test
  public void testAddArgs() throws Exception {

    MethodCall methodCall = MethodCall.of("test");

    assertThat(methodCall.name).isEqualTo("test");
    assertThat(methodCall.args).isEmpty();
  }

  @Test
  public void testAddArgs1() throws Exception {


    MethodCall methodCall = MethodCall.of("test1").with("name", "Jim");

    assertThat(methodCall.name).isEqualTo("test1");
    assertThat(methodCall.args).hasSize(1);
    assertThat(methodCall.args).containsEntry("name", "Jim");
  }

  @Test
  public void testAddArgs2() throws Exception {

    MethodCall methodCall = MethodCall.of("test1").with("name", "Jim", "age", 43L);

    assertThat(methodCall.name).isEqualTo("test1");
    assertThat(methodCall.args).hasSize(2);
    assertThat(methodCall.args)
        .containsEntry("name", "Jim")
        .containsEntry("age", 43L);
  }

  @Test
  public void testAddArgs3() throws Exception {

    MethodCall methodCall = MethodCall.of("test1").with("name", "Jim", "age", 43L, "status", CascadeType.ALL);

    assertThat(methodCall.name).isEqualTo("test1");
    assertThat(methodCall.args).hasSize(3);
    assertThat(methodCall.args)
        .containsEntry("name", "Jim")
        .containsEntry("age", 43L)
        .containsEntry("status", CascadeType.ALL);

  }

}