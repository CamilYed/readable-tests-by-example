package tech.allegro.blog.vinyl.shop.builders.order


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class PayOrderJsonBuilder {
    String orderId = TestData.ORDER_ID
    MoneyJsonBuilder amount = ["amount": 40.00, "currency": "EUR"]

  static PayOrderJsonBuilder aPayment() {
    return new PayOrderJsonBuilder()
  }

  Map toMap() {
    return [
      "cost"    : amount.toMap()
    ]
  }
}
