package io.ebean.mocker;

import org.example.domain.MyBean;
import org.example.domain.MyBeanFinder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class WithStaticFinder_custom_fieldNameTest {

  @Test
  public void testCustomStaticFinderFieldName() {

    TDMyBeanFinder testDouble = new TDMyBeanFinder();

    WithStaticFinder staticFinder = new WithStaticFinder(MyBean.class, "OTHER");
    staticFinder.as(testDouble);

    staticFinder.useTestDouble();
    try {
      MyBean found = MyBean.OTHER.byId(89L);
      assertThat(found).isSameAs(dummyFoo);

    } finally {
      staticFinder.restoreOriginal();
    }
  }

  static MyBean dummyFoo = new MyBean();

  static class TDMyBeanFinder extends MyBeanFinder {

    @Override
    public MyBean byId(Long id) {
      return dummyFoo;
    }
  }
}