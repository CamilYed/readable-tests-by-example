package tech.allegro.blog.vinyl.shop.order

import spock.lang.Specification


class OrderAcceptanceSpec extends Specification {

  // builder client
  // wiremock stub - trait
  // trait

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given: "There is a client order with amount 40 EUR"

    and: "The client has a VIP reputation"

    when: "When the client pays the order of 40 EUR"

    then: "The order has been paid correctly"

    and: "The payment system was notified"

    and: "The free track music was sent to the client's mailbox"
  }

  def "shouldn't charge for delivery for order value above or fixed amount based on promotion price list"() {
    given: "There is a client order with amount 40 EUR"

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

  def "shouldn't modify paid order"() {
    given: "There is a paid client order"

    when: "When the client want to add item to order"

    then: "The payment should reject modification"
  }
}
