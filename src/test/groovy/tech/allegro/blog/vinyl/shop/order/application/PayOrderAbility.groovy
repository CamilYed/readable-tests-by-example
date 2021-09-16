package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.client.domain.GetClientReputationAbility
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisherAbility
import tech.allegro.blog.vinyl.shop.common.time.SetCurrentTimeAbility
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostCalculateAbility
import tech.allegro.blog.vinyl.shop.order.domain.AddOrderAbility
import tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder

import static tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder.anOrderPaidEventWithFreeDelivery

trait PayOrderAbility implements
        SetCurrentTimeAbility,
        AddOrderAbility,
        GetClientReputationAbility,
        DeliveryCostCalculateAbility,
        DomainEventPublisherAbility {

    @Subject
    private OrderPaymentHandler orderPaymentHandler

    void setup() {
        setDefaultCurrentTime()
        orderPaymentHandler = new OrderPaymentHandler(orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher)
    }

    void pay(PayOrderCommandBuilder payOrderCommand) {
        orderPaymentHandler.handle(payOrderCommand.build())
    }
    
    void assertThatClientHasNotPaidForDelivery() {
        assertThatOrderPaidEventWasSentOnce(anOrderPaidEventWithFreeDelivery())
    }

    void assertThatOrderPaidEventWasSentOnce(OrderPaidEventBuilder anEvent) {
        assertThatEventWasPublishedOnce(anEvent.build())
    }
}