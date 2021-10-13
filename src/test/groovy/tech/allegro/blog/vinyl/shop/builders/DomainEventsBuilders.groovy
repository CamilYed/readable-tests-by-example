package tech.allegro.blog.vinyl.shop.builders

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

import java.time.Instant

import static tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid
import static tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseAlreadyPaid
import static tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseDifferentAmounts
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderPaidEventBuilder {
  String clientId = TestData.CLIENT_ID
  String orderId = TestData.ORDER_ID
  Instant when = TestData.DEFAULT_CURRENT_DATE
  Money amount = TestData.EUR_40
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

  OrderPaid build() {
    return new OrderPaid(
      new ClientId(clientId),
      new OrderId(orderId),
      when,
      amount,
      delivery
    )
  }
}


