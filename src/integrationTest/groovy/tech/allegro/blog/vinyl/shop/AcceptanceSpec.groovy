package tech.allegro.blog.vinyl.shop


import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility
import tech.allegro.blog.vinyl.shop.assertions.FreeTrackMusicSenderAssertion

import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThatPayment
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class AcceptanceSpec extends BaseIntegrationTest implements
        CreateOrderAbility,
        ClientReputationAbility,
        OrderPaymentAbility,
        FreeTrackMusicSenderAssertion {

    // @formatter:off
    def "positive payment process with the participation of VIP client"() {
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
            assertThatFreeMusicTrackWasSentToClientOnce()

        // when i go to /order list endpoint i see my order

        // then I should see my paid order
    }

    // @formatter:on
}
