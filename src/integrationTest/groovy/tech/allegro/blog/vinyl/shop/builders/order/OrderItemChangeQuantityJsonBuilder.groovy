package tech.allegro.blog.vinyl.shop.builders.order

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderItemChangeQuantityJsonBuilder {
  String productId
  int quantityChange

  static OrderItemChangeQuantityJsonBuilder anItemToChange() {
    return new OrderItemChangeQuantityJsonBuilder()
  }

  Map toMap() {
    return [
      productId: productId,
      quantity  : quantityChange
    ]
  }
}
