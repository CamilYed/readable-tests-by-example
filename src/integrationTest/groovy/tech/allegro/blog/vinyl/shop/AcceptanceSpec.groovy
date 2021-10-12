package tech.allegro.blog.vinyl.shop

import tech.allegro.blog.vinyl.shop.ability.catalogue.FreeTrackMusicSenderAbility
import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility

import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThat
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class AcceptanceSpec extends BaseIntegrationTest implements
  CreateOrderAbility,
  ClientReputationAbility,
  OrderPaymentAbility,
  FreeTrackMusicSenderAbility {

  // @formatter:off
  def "positive payment process with the participation of VIP client"() {
    given:
        thereIsUnpaid(anOrder())

    and:
        clientIsVip()

        // when: I change the quantity of the item

        // then: Item quantity is changed successfully

        // when: I go to order listing view

        // then: I should see my unpaid order with new item quantity

    when:
        def payment = clientMakeThe(aPayment())

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientHasNotPaidForDelivery()

    and:
        assertThatFreeMusicTrackWasSentToClient()

        // when: I go to order listing view

        // then: I should see my paid order

        // when: I try pay the order

        // then: payment should not be accepted
  }
  // @formatter:on
}
