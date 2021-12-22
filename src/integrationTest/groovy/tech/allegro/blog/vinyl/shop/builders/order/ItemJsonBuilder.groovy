package tech.allegro.blog.vinyl.shop.builders.order

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ItemJsonBuilder {
  String productId = TestData.CZESLAW_NIEMEN_ALBUM_ID
  MoneyJsonBuilder unitPrice
  int quantity

  static ItemJsonBuilder anItem() {
    return new ItemJsonBuilder(
      [
        productId: TestData.CZESLAW_NIEMEN_ALBUM_ID,
        unitPrice: [amount: 40.00, currency: "EUR"],
        quantity : 1
      ]
    )
  }

  ItemJsonBuilder inQuantity(int quantity) {
    this.quantity = quantity
    return this
  }

  Map toMap() {
    return [
      itemUnitPrice: [productId: productId, price: [amount: unitPrice.amount, currency: unitPrice.currency]],
      quantity    : quantity
    ]
  }
}
