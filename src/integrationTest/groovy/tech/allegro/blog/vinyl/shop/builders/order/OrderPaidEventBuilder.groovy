package tech.allegro.blog.vinyl.shop.builders.order

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents
import tech.allegro.blog.vinyl.shop.order.domain.OrderId

import java.time.Instant

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPaidEventBuilder {
    String clientId
    String orderId
    Instant when
    Money amount
    Delivery delivery

    static OrderPaidEventBuilder anOrderPaidEvent() {
        return new OrderPaidEventBuilder()
    }

    OrderPaidEventBuilder withAmountInEuro(String anAmount) {
        amount = Money.of(anAmount, "EUR")
        return this
    }

    OrderPaidEventBuilder withFreeDelivery() {
        delivery = Delivery.freeDelivery()
        return this
    }

    OrderDomainEvents.OrderPaid build() {
        ClientId.of(clientId)
        return OrderDomainEvents.OrderPaid.of(
            ClientId.of(clientId),
            OrderId.of(orderId),
            when,
            Money.of(amount.value, amount.currency),
            delivery
        )
    }
}
