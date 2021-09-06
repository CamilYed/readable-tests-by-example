package tech.allegro.blog.vinyl.shop.builders.order


import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CreateOrderWithItemsJsonBuilder {
  String orderId = "orderId"
  String clientId = "clientId"
  List<ItemJsonBuilder> items = []

  static CreateOrderWithItemsJsonBuilder anOrder() {
    return new CreateOrderWithItemsJsonBuilder()
  }

  CreateOrderWithItemsJsonBuilder withItem(ItemJsonBuilder anItem) {
    items << anItem
    return this
  }

  CreateOrderWithItemsJsonBuilder withItems(List<ItemJsonBuilder> anItems) {
    items += anItems
    return this
  }

  Map toMap() {
    return [
      "orderId" : orderId,
      "clientId": clientId,
      "items"   : items.collect { it.toMap() }
    ]
  }

  @Builder(builderStrategy = SimpleStrategy, prefix = "with")
  static class ItemJsonBuilder {
    String productId
    MoneyJsonBuilder cost

    static ItemJsonBuilder anItem() {
      return new ItemJsonBuilder(productId: "productId", cost: [
        "amount"  : "0.00",
        "currency": "EUR"
      ])
    }

    Map toMap() {
      return [
        "productId": productId,
        "cost"    : cost
      ]
    }
  }
}
