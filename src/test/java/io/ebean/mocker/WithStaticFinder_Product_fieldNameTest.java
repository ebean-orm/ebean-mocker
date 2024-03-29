package io.ebean.mocker;

import org.example.domain.Product;
import org.example.domain.ProductFinder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class WithStaticFinder_Product_fieldNameTest {

  @Test
  public void testAsUseTestDoubleRestoreOriginal() {

    TDProductFinder testDouble = new TDProductFinder();

    WithStaticFinder<Product> staticFinder = new WithStaticFinder<>(Product.class);
    staticFinder.as(testDouble);

    Object original = staticFinder.original;
    assertThat(Product.FIND).isSameAs(original);

    staticFinder.useTestDouble();

    assertThat(Product.FIND).isSameAs(testDouble);
    assertThat(Product.FIND).isNotSameAs(original);

    assertThat(Product.FIND.byId(45L)).isSameAs(dummyFoo);

    staticFinder.restoreOriginal();

    assertThat(Product.FIND).isSameAs(original);
    assertThat(Product.FIND).isNotSameAs(testDouble);

  }

  static Product dummyFoo = new Product();


  class TDProductFinder extends ProductFinder {
    @Override
    public Product byId(Long id) {
      return dummyFoo;
    }
  }

}
