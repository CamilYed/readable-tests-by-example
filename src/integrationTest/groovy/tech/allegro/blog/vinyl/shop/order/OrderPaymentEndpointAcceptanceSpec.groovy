package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.delivery.CourierSystemAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility
import tech.allegro.blog.vinyl.shop.ability.sales.SpecialPriceProviderAbility
import tech.allegro.blog.vinyl.shop.assertions.FreeTrackMusicSenderAssertion

import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThatPayment
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class OrderPaymentEndpointAcceptanceSpec extends BaseIntegrationTest implements
        CreateOrderAbility,
        ClientReputationAbility,
        SpecialPriceProviderAbility,
        CourierSystemAbility,
        OrderPaymentAbility,
        FreeTrackMusicSenderAssertion {

    def "shouldn't charge for delivery when the client has a VIP reputation"() {
        given:
            thereIsUnpaid(anOrder())

        and:
            clientIsVip()

        when:
            def payment = clientMakeThe(aPayment())

        then:
            assertThatPayment(payment).madeSuccessfully()

        and:
            assertThatClientHasNotPaidForDelivery()

        and:
            assertThatFreeMusicTrackWasSentToClient()
    }

    def "shouldn't charge for delivery for order value above or fixed amount based on promotion price list"() {
        given:
            thereIsUnpaid(anOrder().withAmount(euro(40.00)))

        and:
            clientIsNotVip()

        and:
            minimumOrderValueForFreeDeliveryIs(euro(39.99))

        when:
            def payment = clientMakeThe(aPayment().withAmount(euro(40.00)))

        then:
            assertThatPayment(payment).madeSuccessfully()

        and:
            assertThatClientHasNotPaidForDelivery()

        and:
            assertThatFreeMusicTrackWasNotSentToClient()
    }

    def "should charge for delivery based on price provided by courier system"() {
        given:
            thereIsUnpaid(anOrder().withAmount(euro(40.00)))

        and:
            clientIsNotVip()

        and:
            currentDeliveryCostIs(euro(30.00))

        and:
            minimumOrderValueForFreeDeliveryIs(euro(50.00))

        when:
            def payment = clientMakeThe(aPayment().withAmount(euro(70.00)))

        then:
            assertThatPayment(payment).madeSuccessfully()

        and:
            assertThatClientPaidForDeliveryWithAmount(euro(30.00))

        and:
            assertThatFreeMusicTrackWasNotSentToClient()
    }

    def "should charge always 20 euro for delivery when the courier system is unavailable"() {
        given: "There is a client order with amount 40 EUR"

        and: "The client is not a VIP"

        and: "Free delivery is valid from an amount equal to 50 EUR"

        and: "The courier system is unavailable and default price of delivery is 20 EUR"

        when: "When the client pays the order of 40 EUR"

        then: "The order has been paid correctly with delivery cost equal to 20 EUR"

        and: "The payment system was notified"

        and: "The free music track was not sent to the client's mailbox"
    }

    def "shouldn't accept payment if the amounts differ"() {

    }
}