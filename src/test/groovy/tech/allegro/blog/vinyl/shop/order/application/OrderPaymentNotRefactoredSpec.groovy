package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider
import tech.allegro.blog.vinyl.shop.delivery.domain.CurrentDeliveryCostProvider
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy
import tech.allegro.blog.vinyl.shop.order.domain.FreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPaidEvent
import static tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy.DefaultDeliveryCostPolicy
import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand

class OrderPaymentNotRefactoredSpec extends Specification {

  OrderRepository orderRepository = Stub()
  ClientReputationProvider clientReputationProvider = Stub()
  CurrentDeliveryCostProvider currentDeliveryCostProvider = Stub()
  SpecialPriceProvider specialPriceProvider = Stub()
  DeliveryCostPolicy deliveryCostPolicy = new DefaultDeliveryCostPolicy(currentDeliveryCostProvider, specialPriceProvider)
  DomainEventPublisher domainEventPublisher = Mock()
  FreeMusicTrackSender mailBoxSystemBox = Mock()

  @Subject
  OrderPaymentHandler paymentHandler = new OrderPaymentHandler(
    orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher, mailBoxSystemBox
  )

  static final Instant CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")
  static final Clock TEST_CLOCK = Clock.fixed(CURRENT_DATE, ZoneId.systemDefault())
  static final Money _20_EUR = Money.of(20.00)
  static final Money _25_EUR = Money.of(25.00)
  static final Money _40_EUR = Money.of(40.00)
  static final Money _50_EUR = Money.of(50.00)
  static final VinylId PRODUCT_ID = VinylId.of("1")
  static final OrderId ORDER_ID = OrderId.of("1")
  static final Order ORDER_40_EUR = sampleOrder(_40_EUR)
  static final ClientId CLIENT_ID = ClientId.of("1")
  static final ClientReputation VIP = ClientReputation.vip(CLIENT_ID)
  static final ClientReputation NOT_VIP = ClientReputation.notVip(CLIENT_ID)
  static final PayOrderCommand PAY_FOR_ORDER_40_EUR = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR)
  static final PayOrderCommand PAY_FOR_ORDER_40_EUR_PLUS_20_EUR_DELIVERY = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR.add(_20_EUR))
  static final PayOrderCommand PAY_FOR_ORDER_40_EUR_PLUS_25_EUR_DELIVERY = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR.add(_25_EUR))

  def setupSpec() {
    ClockProvider.setSystemClock(TEST_CLOCK)
  }

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(ORDER_40_EUR)

    and:
        clientReputationProvider.get(CLIENT_ID) >> VIP

    when:
        paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

    then:
        noExceptionThrown()

    then:
        1 * domainEventPublisher.publish({ OrderPaidEvent event ->
          assert event.orderId == ORDER_ID
          assert event.amount == _40_EUR
          assert event.delivery.cost == Money.ZERO
          assert event.when == CURRENT_DATE
        })

    and:
        1 * mailBoxSystemBox.send(CLIENT_ID)
  }

  def "shouldn't charge for delivery for order value above amount based on promotion price list"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(ORDER_40_EUR)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _40_EUR

    when:
        paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

    then:
        noExceptionThrown()

    then:
        1 * domainEventPublisher.publish({ OrderPaidEvent event ->
          assert event.orderId == ORDER_ID
          assert event.amount == _40_EUR
          assert event.delivery.cost == Money.ZERO
          assert event.when == CURRENT_DATE
        })

    and:
        0 * mailBoxSystemBox.send(CLIENT_ID)
  }

  def "should charge for delivery based on price provided by courier system"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(ORDER_40_EUR)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _50_EUR

    and:
        currentDeliveryCostProvider.currentCost() >> _25_EUR

    when:
        paymentHandler.handle(PAY_FOR_ORDER_40_EUR_PLUS_25_EUR_DELIVERY)

    then:
        noExceptionThrown()

    and:
        1 * domainEventPublisher.publish({ OrderPaidEvent event ->
          assert event.orderId == ORDER_ID
          assert event.amount == _40_EUR
          assert event.delivery.cost == _25_EUR
          assert event.when == CURRENT_DATE
        })

    and:
        0 * mailBoxSystemBox.send(CLIENT_ID)
  }

  def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(ORDER_40_EUR)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _50_EUR

    and:
        currentDeliveryCostProvider.currentCost() >> { throw new RuntimeException() }

    when:
        paymentHandler.handle(PAY_FOR_ORDER_40_EUR_PLUS_20_EUR_DELIVERY)

    then:
        noExceptionThrown()

    and:
        1 * domainEventPublisher.publish({ OrderPaidEvent event ->
          assert event.orderId == ORDER_ID
          assert event.amount == _40_EUR
          assert event.delivery.cost == _20_EUR
          assert event.when == CURRENT_DATE
        })

    and:
        0 * mailBoxSystemBox.send(CLIENT_ID)
  }

  private static Order sampleOrder(Money price) {
    def order = new Order(ORDER_ID, null, true)
    order.addItem(PRODUCT_ID, price)
    return order
  }
}
