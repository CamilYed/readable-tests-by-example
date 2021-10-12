package tech.allegro.blog.vinyl.shop.builders.order

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ItemJsonBuilder {
  String productId = TestData.CZESLAW_NIEMEN_ALBUM_ID
  MoneyJsonBuilder cost
  int quantity = 1

  static ItemJsonBuilder anItem() {
    return new ItemJsonBuilder(
      [
        productId: TestData.CZESLAW_NIEMEN_ALBUM_ID,
        cost     : [amount: 40.00, currency: "EUR"],
        quantity : 1
      ]
    )
  }

  Map toMap() {
    return [
      itemUnitCost: [productId: productId, cost: [amount: cost.amount, currency: cost.currency]],
      quantity    : quantity
    ]
  }
}
