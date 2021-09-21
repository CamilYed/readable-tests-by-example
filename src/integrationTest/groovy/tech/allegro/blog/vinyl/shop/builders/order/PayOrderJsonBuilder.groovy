package tech.allegro.blog.vinyl.shop.builders.order

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class PayOrderJsonBuilder {
    String orderId = TestData.ORDER_ID
    String clientId = TestData.CLIENT_ID
    MoneyJsonBuilder cost = ["amount": "40.00", "currency": "EUR"]

    static PayOrderJsonBuilder aPayment() {
        return new PayOrderJsonBuilder()
    }

    Map toMap() {
        return [
                "clientId": clientId,
                "cost"    : cost.toMap()
        ]
    }
}
