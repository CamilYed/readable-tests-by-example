package tech.allegro.blog.vinyl.shop.common.money


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class MoneyBuilder {
  BigDecimal amount
  String currency

  static MoneyBuilder euro(BigDecimal amount) {
    return new MoneyBuilder(amount: amount, currency: "EUR")
  }

  Money build() {
    return new Money(amount, Currency.getInstance(currency))
  }
}
