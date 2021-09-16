package tech.allegro.blog.vinyl.shop.order.application

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.order.domain.OrderId

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class PayOrderCommandBuilder {
    String orderId  = "CLIENT_ID_001"
    String clientId = "ORDER_ID_001"
    String amount = "40.00"
    String currency = "EUR"

    static PayOrderCommandBuilder aPayOrderCommand() {
        return new PayOrderCommandBuilder()
    }

    OrderPaymentHandler.PayOrderCommand build() {
        return OrderPaymentHandler.PayOrderCommand.of(
                ClientId.of(clientId),
                OrderId.of(orderId),
                Money.of(amount, currency)
        )
    }
}
