package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.abilities.PayOrderAbility

import static tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.PayOrderCommandBuilder.aPayment
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.aPaidOrder
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.anUnpaidOrder

class OrderPaymentHandlerSpec extends Specification implements PayOrderAbility {

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given:
        thereIs(anUnpaidOrder())

    and:
        clientIsVip()

    when:
        clientMakeThe(aPayment())

    then:
        assertThatClientHasNotPaidForDelivery()
  }

  def "shouldn't charge for delivery when order value is above amount based on promotion price list"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        minimumOrderValueForFreeDeliveryIs(euro(30.00))

    and:
        clientIsNotVip()

    when:
        clientMakeThe(aPayment().withAmount(euro(40.00)))

    then:
        assertThatClientHasNotPaidForDelivery()
  }

  def "should charge for delivery based on price provided by courier system"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        clientIsNotVip()

    and:
        currentDeliveryCostIs(euro(12.00))

    when:
        clientMakeThe(aPayment().withAmount(euro(52.00)))

    then:
        assertThatClientPaidForDeliveryWithAmount(euro(12.00))
  }

  def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(40.00)))

    and:
        externalCourierSystemIsUnavailable()

    when:
        clientMakeThe(aPayment().withAmount(euro(60.00)))

    then:
        assertThatClientPaidForDeliveryWithAmount(euro(20.00))
  }

  def "shouldn't accept payment if the order already paid"() {
    given:
        thereIs(aPaidOrder())

    when:
        def paymentResult = clientMakeThe(aPayment())

    then:
        assertThatPaymentNotAcceptedBecauseOrderAlreadyPaid(paymentResult)
  }

  def "shouldn't accept payment if the amounts differ"() {
    given:
        thereIs(anUnpaidOrder().withAmount(euro(10.00)))

    when:
        def paymentResult = clientMakeThe(aPayment().withAmount(euro(9.00)))

    then:
        assertThatPaymentNotAcceptedBecauseDifferentAmounts(paymentResult)
  }
}
