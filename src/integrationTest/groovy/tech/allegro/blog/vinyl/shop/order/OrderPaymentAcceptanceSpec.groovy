package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.catalogue.FreeMusicTrackSenderAbility
import tech.allegro.blog.vinyl.shop.ability.client.ClientReputationAbility
import tech.allegro.blog.vinyl.shop.ability.delivery.CourierSystemAbility
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderPaymentAbility
import tech.allegro.blog.vinyl.shop.ability.sales.SpecialPriceProviderAbility

import static tech.allegro.blog.vinyl.shop.assertions.PaymentResultAssertion.assertThat
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anUnpaidOrder
import static tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder.aPayment

class OrderPaymentAcceptanceSpec extends BaseIntegrationTest implements
  CreateOrderAbility,
  ClientReputationAbility,
  SpecialPriceProviderAbility,
  CourierSystemAbility,
  OrderPaymentAbility,
  FreeMusicTrackSenderAbility {

  def setup() {
    clientIsNotVip()
    minimumOrderValueForFreeDeliveryIs(euro(80.00))
  }

  def "shouldn't charge for delivery when the client has a VIP reputation"() {
    given:
        thereIs(anUnpaidOrder())

    and:
        clientIsVip()

    when:
        def payment = clientMakesThe(aPayment())

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientDidNotPaidForDelivery()

    and:
        assertThatFreeMusicTrackWasSentToTheClient()
  }

  def "shouldn't charge for delivery for order value above or fixed amount based on promotion price list"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        minimumOrderValueForFreeDeliveryIs(euro(39.99))

    when:
        def payment = clientMakesThe(aPayment().inTheAmountOf(euro(40.00)))

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientDidNotPaidForDelivery()

    and:
        assertThatFreeMusicTrackWasNotSentToTheClient()
  }

  def "should charge for delivery based on price provided by courier system"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        currentDeliveryCostIs(euro(30.00))

    and:
        minimumOrderValueForFreeDeliveryIs(euro(50.00))

    when:
        def payment = clientMakesThe(aPayment().inTheAmountOf(euro(70.00)))

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientPaidForDeliveryInTheAmountOf(euro(30.00))

    and:
        assertThatFreeMusicTrackWasNotSentToTheClient()
  }

  def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        externalCourierSystemIsUnavailable()

    when:
        def payment = clientMakesThe(aPayment().inTheAmountOf(euro(60.00)))

    then:
        assertThat(payment).succeeded()

    and:
        assertThatClientPaidForDeliveryInTheAmountOf(euro(20.00))

    and:
        assertThatFreeMusicTrackWasNotSentToTheClient()
  }

  def "shouldn't accept payment if the amounts differ"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(10.00)))

    and:
        currentDeliveryCostIs(euro(30.00))

    when:
        def payment = clientMakesThe(aPayment().inTheAmountOf(euro(39.00)))

    then:
        assertThat(payment)
          .failed()
          .dueToDifferentAmounts("Incorrect amount, difference is: -1.00 !")
  }
}
