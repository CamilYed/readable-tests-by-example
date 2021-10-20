package tech.allegro.blog.vinyl.shop

import tech.allegro.blog.vinyl.shop.ability.catalogue.FreeTrackMusicSenderAbility
import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderListingAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder
import tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder
import tech.allegro.blog.vinyl.shop.common.money.Money

import static tech.allegro.blog.vinyl.shop.TestData.CZESLAW_NIEMEN_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.TestData.ORDER_ID
import static tech.allegro.blog.vinyl.shop.assertions.OrdersViewAssertion.assertThatView
import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThat
import static tech.allegro.blog.vinyl.shop.builders.OrderPaidEventBuilder.anOrderPaidEvent
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class AcceptanceSpec extends BaseIntegrationTest implements
  CreateOrderAbility,
  ClientReputationAbility,
  OrderPaymentAbility,
  FreeTrackMusicSenderAbility,
  OrderListingAbility {

  // @formatter:off
  def "positive payment process with the participation of the VIP client"() {
    given:
        thereIsUnpaid(anOrder()
                        .withOrderId(ORDER_ID)
                        .withItem(anItem()
                                    .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                                    .withUnitPrice(euro(14.00))
                                    .inQuantity(10)
                        )
        )

    and:
        clientIsVip()

    when:
        def payment = clientMakeThe(aPayment().inTheAmount(euro(140.00)))

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientNotPaidForDelivery(anOrderPaidEvent()
                                                .withFreeDelivery()
                                                .withAmount(Money.euro(140.00))
        )

    and:
        assertThatFreeMusicTrackWasSentToClient()

    when:
      def orderView = listOrder()

    then:
        assertThatView(orderView)
          .hasOrderIdThat(ORDER_ID)
            .hasItemThat(CZESLAW_NIEMEN_ALBUM_ID)
            .withUnitPrice(euro(14.00))
            .withQuantity(10)

        // when: I go to order listing view

        // then: I should see my paid order

        // when: I try pay the order

        // then: payment should not be accepted
  }
  // @formatter:on
}
