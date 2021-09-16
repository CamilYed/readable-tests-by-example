package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

import java.time.Instant

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPaidEventBuilder {
    String clientId = "CLIENT_ID_001"
    String orderId = "ORDER_ID_001"
    Instant when = Instant.parse("2021-11-05T00:00:00.00Z")
    Money amount = Money.of("40.00", "EUR")
    Delivery delivery

    static OrderPaidEventBuilder anOrderPaidEventWithFreeDelivery() {
        anOrderPaidEvent().withDelivery(Delivery.freeDelivery())
    }

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
        return OrderDomainEvents.OrderPaid.of(
                ClientId.of(clientId),
                OrderId.of(orderId),
                when,
                Money.of(amount.value, amount.currency),
                delivery
        )
    }
}
