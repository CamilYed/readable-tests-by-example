package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification

import static tech.allegro.blog.vinyl.shop.order.application.PayOrderCommandBuilder.aPayOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.OrderAggregateBuilder.anUnpaidOrder

class OrderPaymentHandlerSpec extends Specification implements PayOrderAbility {

    static final ORDER_ID = "ORDER_ID_001"
    static final CLIENT_ID = "CLIENT_ID_001"

    def "shouldn't charge for delivery when the client has a VIP status"() {
        given:
            thereIs(anUnpaidOrder()
                    .withId(ORDER_ID)
                    .withClientId(CLIENT_ID)
            )

        and:
            clientWithIdIsVip(CLIENT_ID)

        when:
            pay(aPayOrderCommand()
                    .withOrderId(ORDER_ID)
                    .withClientId(CLIENT_ID)
            )

        then:
            assertThatClientHasNotPaidForDelivery()
    }

    def "shouldn't charge for delivery for order value above amount based on promotion price list"() {

    }

    def "should charge for delivery based on price provided by courier system"() {

    }

    def "shouldn't charge for a previously paid order"() {

    }

    def "should charge always 20 euro for delivery when the courier system is unavailable"() {

    }

    def "shouldn't accept payment if the amounts differ"() {

    }
}
