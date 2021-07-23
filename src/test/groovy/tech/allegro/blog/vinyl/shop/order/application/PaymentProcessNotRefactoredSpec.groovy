package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.VinylId
import tech.allegro.blog.vinyl.shop.client.ClientId
import tech.allegro.blog.vinyl.shop.client.ClientReputation
import tech.allegro.blog.vinyl.shop.client.ClientReputationProvider
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.Delivery
import tech.allegro.blog.vinyl.shop.delivery.DeliveryCostPolicy
import tech.allegro.blog.vinyl.shop.order.adapters.MailBoxSystemBox
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository
import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand

class PaymentProcessNotRefactoredSpec extends Specification {

  OrderRepository orderRepository = Stub()
  ClientReputationProvider clientReputationProvider = Stub()
  DeliveryCostPolicy deliveryCostPolicy = Stub()
  DomainEventPublisher domainEventPublisher = Mock()
  MailBoxSystemBox mailBoxSystemBox = Mock()

  @Subject
  OrderPaymentHandler paymentHandler = new OrderPaymentHandler(
    orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher, mailBoxSystemBox
  )

  static final Money _40_EUR = Money.of(40.00)
  static final VinylId PRODUCT_ID = VinylId.of("1")
  static final OrderId ORDER_ID = OrderId.of("1")
  static final Order ORDER_40_EUR = sampleOrder(Money.of(40.00))
  static final ClientId CLIENT_ID = ClientId.of("1")
  static final ClientReputation VIP = ClientReputation.VIP
  static final PayOrderCommand PAY_FOR_ORDER_40_EUR = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR)

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(ORDER_40_EUR)

    and:
        clientReputationProvider.getFor(CLIENT_ID) >> VIP

    and:
        deliveryCostPolicy.calculate(_40_EUR, VIP) >> Delivery.freeDeliveryDueToClientReputation()

    when:
        paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

    then:
        noExceptionThrown()

    and:
        1 * domainEventPublisher.saveAndPublish(_)

    and:
        1 * mailBoxSystemBox.sendFreeMusicTrackForClient(CLIENT_ID)
  }

  def "shouldn't charge for delivery for order value above fixed amount based on promotion price list"() {
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

  private static Order sampleOrder(Money price) {
    def order = new Order(ORDER_ID, null, true)
    order.addItem(PRODUCT_ID, price)
    return order
  }
}
