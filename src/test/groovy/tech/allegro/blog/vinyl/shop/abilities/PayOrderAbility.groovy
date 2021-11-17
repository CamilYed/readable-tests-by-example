package tech.allegro.blog.vinyl.shop.abilities

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.common.result.Result
import tech.allegro.blog.vinyl.shop.common.time.SetCurrentTimeAbility
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler
import tech.allegro.blog.vinyl.shop.builders.PayOrderCommandBuilder
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.builders.OrderPaidEventBuilder

import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.IncorrectAmount
import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.OrderAlreadyPaid
import static tech.allegro.blog.vinyl.shop.builders.OrderPaidEventBuilder.anOrderPaidEvent
import static tech.allegro.blog.vinyl.shop.builders.OrderPaidEventBuilder.anOrderPaidEventWithFreeDelivery

trait PayOrderAbility implements
  SetCurrentTimeAbility,
  OrderAbility,
  GetClientReputationAbility,
  DeliveryCostCalculateAbility,
  DomainEventPublisherAbility {

  @Subject
  private OrderPaymentHandler orderPaymentHandler

  def setup() {
    setDefaultCurrentTime()
    clientIsNotVip()
    orderPaymentHandler = new OrderPaymentHandler(
      orderRepository,
      new OrderFactory(),
      clientReputationProvider,
      deliveryCostPolicy,
      domainEventPublisher
    )
  }

  Result<Void> clientMakeThe(PayOrderCommandBuilder payOrderCommand) {
    return orderPaymentHandler.handle(payOrderCommand.build())
  }

  void assertThatClientPaidForDeliveryInTheAmount(MoneyBuilder anAmount) {
    assertThatEventWasPublishedOnce(
      anOrderPaidEvent()
        .withDelivery(new Delivery(anAmount.build()))
        .build()
    )
  }

  void assertThatPaymentNotAcceptedBecauseOrderAlreadyPaid(Result<Void> paymentResult) {
    assertThatAnyEventWasNotPublished()
    assert paymentResult.error().cause() instanceof OrderAlreadyPaid
  }

  void assertThatPaymentNotAcceptedBecauseDifferentAmounts(Result<Void> paymentResult) {
    assertThatAnyEventWasNotPublished()
    assert paymentResult.error().cause() instanceof IncorrectAmount
  }

  void assertThatClientDitNotPaidForDelivery() {
    assertThatOrderWasPaid(anOrderPaidEventWithFreeDelivery())
  }

  void assertThatOrderWasPaid(OrderPaidEventBuilder anEvent) {
    assertThatEventWasPublishedOnce(anEvent.build())
  }
}
