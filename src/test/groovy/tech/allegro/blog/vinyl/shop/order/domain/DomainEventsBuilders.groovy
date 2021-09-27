package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

import java.time.Instant

import static tech.allegro.blog.vinyl.shop.order.domain.Values.*

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPaidEventBuilder {
    String clientId = TestData.CLIENT_ID
    String orderId = TestData.ORDER_ID
    Instant when = TestData.DEFAULT_CURRENT_DATE
    Money amount = TestData._40_EUR
    Delivery delivery

    static OrderPaidEventBuilder anOrderPaidEventWithFreeDelivery() {
        anOrderPaidEvent().withFreeDelivery()
    }

    static OrderPaidEventBuilder anOrderPaidEvent() {
        return new OrderPaidEventBuilder()
    }

    OrderPaidEventBuilder withFreeDelivery() {
        delivery = Delivery.freeDelivery()
        return this
    }

    OrderDomainEvents.OrderPaid build() {
        return new OrderDomainEvents.OrderPaid(
                ClientId.of(clientId),
                OrderId.of(orderId),
                when,
                amount,
                delivery
        )
    }
}

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPayFailedEventBuilder {
    String orderId = TestData.ORDER_ID
    Instant when = TestData.DEFAULT_CURRENT_DATE
    String reason

    static OrderPayFailedEventBuilder anOrderAlreadyPaid() {
        return new OrderPayFailedEventBuilder().withReason("ALREADY_PAID")
    }

    static OrderPayFailedEventBuilder aDifferentAmount() {
        return new OrderPayFailedEventBuilder().withReason("AMOUNT_IS_DIFFERENT")
    }

    OrderDomainEvents.OrderFailureEvent build() {
        if ("AMOUNT_IS_DIFFERENT" == reason) {
            return new OrderDomainEvents.OrderPayFailedBecauseAmountIsDifferent(
                    OrderId.of(orderId),
                    when
            )
        } else {
            return new OrderDomainEvents.OrderPayFailedBecauseAlreadyPaid(
                    OrderId.of(orderId),
                    when
            )
        }
    }
}
