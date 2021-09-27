package tech.allegro.blog.vinyl.shop.order.application

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder

import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class PayOrderCommandBuilder {
    String orderId = TestData.ORDER_ID
    String clientId = TestData.CLIENT_ID
    Money amount = TestData._40_EUR

    static PayOrderCommandBuilder aPayment() {
        return new PayOrderCommandBuilder()
    }

    PayOrderCommandBuilder withAmount(MoneyBuilder anAmount) {
        this.amount = anAmount.build()
        return this
    }

    OrderPaymentHandler.PayOrderCommand build() {
        return new OrderPaymentHandler.PayOrderCommand(
                new ClientId(clientId),
                new OrderId(orderId),
                amount
        )
    }
}
