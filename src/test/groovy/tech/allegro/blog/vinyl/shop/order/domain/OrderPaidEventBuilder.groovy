package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

import java.time.Instant

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPaidEventBuilder {
    String clientId = TestData.CLIENT_ID
    String orderId = TestData.ORDER_ID
    Instant when = TestData.DEFAULT_CURRENT_DATE
    Money amount = TestData._40_EUR
    Delivery delivery

    static OrderPaidEventBuilder anOrderPaidEventWithFreeDelivery() {
        anOrderPaidEvent().withDelivery(Delivery.freeDelivery())
    }

    static OrderPaidEventBuilder anOrderPaidEvent() {
        return new OrderPaidEventBuilder()
    }

    OrderPaidEventBuilder withAmountInEuro(BigDecimal anAmount) {
        amount = Money.of(anAmount, TestData.EURO_CURRENCY)
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
