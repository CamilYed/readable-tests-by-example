package tech.allegro.blog.vinyl.shop.builders.money


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class MoneyJsonBuilder {
    Double amount
    String currency

    static MoneyJsonBuilder euro(Double amount) {
        return new MoneyJsonBuilder(amount: amount, currency: "EUR")
    }

    Map toMap() {
        return [
                "amount"  : amount,
                "currency": currency
        ]
    }
}
