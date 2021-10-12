package tech.allegro.blog.vinyl.shop.common.money

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.common.volume.Quantity

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class QuantityBuilder {
  int value = 1

  static QuantityBuilder quantity(int value) {
    return new QuantityBuilder()
  }

  Quantity build() {
    return new Quantity(value)
  }
}
