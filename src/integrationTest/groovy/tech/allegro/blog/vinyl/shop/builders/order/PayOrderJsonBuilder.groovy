package tech.allegro.blog.vinyl.shop.builders.order


import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

class PayOrderJsonBuilder {
    String orderId = TestData.ORDER_ID
    MoneyJsonBuilder amount = ["amount": 40.00, "currency": "EUR"]

  static PayOrderJsonBuilder aPayment() {
    return new PayOrderJsonBuilder()
  }

  PayOrderJsonBuilder inTheAmountOf(MoneyJsonBuilder amount) {
    this.amount = amount
    return this
  }

  Map toMap() {
    return [
      "cost"    : amount.toMap()
    ]
  }
}
