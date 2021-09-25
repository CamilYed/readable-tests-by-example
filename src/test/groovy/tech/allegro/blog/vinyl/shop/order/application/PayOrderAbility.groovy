package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.client.domain.GetClientReputationAbility
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisherAbility
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.common.time.SetCurrentTimeAbility
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostCalculateAbility
import tech.allegro.blog.vinyl.shop.order.domain.AddOrderAbility
import tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder

import static tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder.anOrderPaidEvent
import static tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder.anOrderPaidEventWithFreeDelivery
import static tech.allegro.blog.vinyl.shop.order.domain.OrderPayFailedEventBuilder.aDifferentAmount
import static tech.allegro.blog.vinyl.shop.order.domain.OrderPayFailedEventBuilder.anOrderAlreadyPaid

trait PayOrderAbility implements
        SetCurrentTimeAbility,
        AddOrderAbility,
        GetClientReputationAbility,
        DeliveryCostCalculateAbility,
        DomainEventPublisherAbility {

    @Subject
    private OrderPaymentHandler orderPaymentHandler

    def setup() {
        setDefaultCurrentTime()
        clientIsNotVip()
        orderPaymentHandler = new OrderPaymentHandler(orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher)
    }

    void clientMakeThe(PayOrderCommandBuilder payOrderCommand) {
        orderPaymentHandler.handle(payOrderCommand.build())
    }

    void assertThatClientPaidForDeliveryWithAmount(MoneyBuilder anAmount) {
        assertThatEventWasPublishedOnce(anOrderPaidEvent()
                                            .withDelivery(Delivery.of(anAmount.build()))
                                            .build()
        )
    }

    void assertThatPaymentNotAcceptedBecauseOrderAlreadyPaid() {
        assertThatEventWasPublishedOnce(anOrderAlreadyPaid().build())
    }

    void assertThatPaymentNotAcceptedBecauseDifferentAmounts() {
        assertThatEventWasPublishedOnce(aDifferentAmount().build())
    }

    void assertThatClientHasNotPaidForDelivery() {
        assertThatOrderWasPaid(anOrderPaidEventWithFreeDelivery())
    }

    void assertThatOrderWasPaid(OrderPaidEventBuilder anEvent) {
        assertThatEventWasPublishedOnce(anEvent.build())
    }
}