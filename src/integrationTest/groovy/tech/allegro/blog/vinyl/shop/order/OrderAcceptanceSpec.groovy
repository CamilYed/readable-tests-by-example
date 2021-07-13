package tech.allegro.blog.vinyl.shop.order

import spock.lang.Specification


class OrderAcceptanceSpec extends Specification {


  def "shouldn't charge for delivery when the client has a VIP status"() {
    given: "There is a client order with amount 40 euro"

    and: "The client has a VIP reputation"

    when: "When the client pays the order with the 40 euro"

    then: "The order has been paid correctly"

    and: "The payment system has been notified"
  }

  def "shouldn't charge for delivery for order value above fixed amount based on promotion price list"() {

  }

  def "should charge for delivery based on price provided by courier system"() {

  }

  def "should charge always 20 euro for delivery when the courier system is unavailable"() {

  }

  def "should send free music track to VIP client mailbox"() {

  }

  def "shouldn't modify paid order"() {

  }
}
