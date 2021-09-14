package tech.allegro.blog.vinyl.shop


import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility
import tech.allegro.blog.vinyl.shop.assertions.DomainEventPublisherAssertion
import tech.allegro.blog.vinyl.shop.assertions.FreeTrackMusicSenderAssertion

import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThatPayment
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.ItemJsonBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.OrderPaidEventBuilder.anOrderPaidEvent
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class AcceptanceSpec extends BaseIntegrationTest
        implements CreateOrderAbility, ClientReputationAbility, OrderPaymentAbility,
                DomainEventPublisherAssertion, FreeTrackMusicSenderAssertion {

    static final String ORDER_ID = "ORDER_ID_001"
    static final String VIP_CLIENT_ID = "VIP_CLIENT_001"
    static final String CZESLAW_NIEMEN_ALBUM_ID = "PRODUCT_ID_001"

    // @formatter:off
    def "shouldn't charge for delivery when the client has a VIP reputation"() {
        given:
            thereIs(
                anOrder()
                    .withOrderId(ORDER_ID)
                    .withClientId(VIP_CLIENT_ID)
                    .withItem(anItem()
                        .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                        .withCost(euro("40.00")))
            )

        and:
            aClientHasReputationAsVip(VIP_CLIENT_ID)

        when:
            def payment = makeThe(aPayment()
                                    .withOrderId(ORDER_ID)
                                    .withClientId(VIP_CLIENT_ID)
                                    .withCost(euro("40.00"))
            )

        then:
            assertThatPayment(payment).madeSuccessfully()

        and:
            assertThatNotificationAboutSuccessfulPaymentWasSentOnce(
                anOrderPaidEvent()
                    .withClientId(VIP_CLIENT_ID)
                    .withOrderId(ORDER_ID)
                    .withAmountInEuro("40.00")
                    .withFreeDelivery()
                    .withWhen(CURRENT_DATE)
            )

        and:
            assertThatFreeMusicTrackWasSentToClientOnce(VIP_CLIENT_ID)
    }
    // @formatter:on

    def "shouldn't charge for delivery for order value above or fixed amount based on promotion price list"() {
        given:
            thereIs anOrder()
                    .withOrderId(ORDER_ID)
                    .withClientId(VIP_CLIENT_ID)
                    .withItem(anItem()
                            .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                            .withCost(euro("40.00")))

        and: "The client is not a VIP"

        and: "Free delivery is valid from an amount equal to 40 EUR"

        when: "When the client pays the order of 40 EUR"

        then: "The order has been paid correctly"

        and: "The payment system was notified"

        and: "The free music track was not sent to the client's mailbox"
    }

    def "should charge for delivery based on price provided by courier system"() {
        given: "There is a client order with amount 40 EUR"

        and: "The client is not a VIP"

        and: "Free delivery is valid from an amount equal to 50 EUR"

        and: "The delivery costs according to the courier's price list equal to 25 EUR"

        when: "When the client pays the order of 40 EUR"

        then: "The order has been paid correctly with delivery cost equal to 25 EUR"

        and: "The payment system was notified"

        and: "The free music track was not sent to the client's mailbox"
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
