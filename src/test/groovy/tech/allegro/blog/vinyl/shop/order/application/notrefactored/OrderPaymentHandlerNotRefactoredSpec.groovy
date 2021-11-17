package tech.allegro.blog.vinyl.shop.order.application.notrefactored

import org.apache.groovy.util.Maps
import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider
import tech.allegro.blog.vinyl.shop.common.volume.Quantity
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostProvider
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy.DefaultDeliveryCostPolicy
import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class OrderPaymentHandlerNotRefactoredSpec extends Specification {

  OrderRepository orderRepository = Stub()
  ClientReputationProvider clientReputationProvider = Stub()
  DeliveryCostProvider currentDeliveryCostProvider = Stub()
  SpecialPriceProvider specialPriceProvider = Stub()
  DeliveryCostPolicy deliveryCostPolicy = new DefaultDeliveryCostPolicy(currentDeliveryCostProvider, specialPriceProvider)
  DomainEventPublisher domainEventPublisher = Mock()
  OrderFactory orderFactory = new OrderFactory()

  @Subject
  OrderPaymentHandler paymentHandler = new OrderPaymentHandler(
    orderRepository,
    new OrderFactory(),
    clientReputationProvider,
    deliveryCostPolicy,
    domainEventPublisher
  )

  final Instant CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")
  final Clock TEST_CLOCK = Clock.fixed(CURRENT_DATE, ZoneId.systemDefault())
  final Money EUR_20 = Money.of("20.00", "EUR")
  final Money EUR_25 = Money.of("25.00", "EUR")
  final Money EUR_40 = Money.of("40.00", "EUR")
  final Money EUR_50 = Money.of("50.00", "EUR")
  final ClientId CLIENT_ID = new ClientId("1")
  final Vinyl VINYL_1 = new Vinyl(new VinylId("1"), EUR_40)
  final Quantity ONE = new Quantity(1)
  final OrderId ORDER_ID = new OrderId("1")
  final OrderDataSnapshot UNPAID_ORDER_EUR_40 = orderFactory.create(ORDER_ID, CLIENT_ID, Maps.of(VINYL_1, ONE), true).toSnapshot()
  final OrderDataSnapshot PAID_ORDER = orderFactory.create(ORDER_ID, CLIENT_ID, Maps.of(VINYL_1, ONE), false).toSnapshot()
  final ClientReputation VIP = ClientReputation.vip(CLIENT_ID)
  final ClientReputation NOT_VIP = ClientReputation.notVip(CLIENT_ID)
  final PayOrderCommand PAY_FOR_ORDER_EUR_40 = new PayOrderCommand(ORDER_ID, EUR_40)
  final PayOrderCommand PAY_FOR_ORDER_EUR_40_PLUS_20_EUR_DELIVERY = new PayOrderCommand(ORDER_ID, EUR_40.add(EUR_20))
  final PayOrderCommand PAY_FOR_ORDER_EUR_40_PLUSEUR_25_DELIVERY = new PayOrderCommand(ORDER_ID, EUR_40.add(EUR_25))

  def setup() {
    ClockProvider.setSystemClock(TEST_CLOCK)
  }

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_EUR_40)

    and:
        clientReputationProvider.get(CLIENT_ID) >> VIP

    when:
        def result = paymentHandler.handle(PAY_FOR_ORDER_EUR_40)

    then:
        result.isSuccess()

    and:
        1 * domainEventPublisher.publish({ OrderPaid event ->
          assert event.orderId() == ORDER_ID
          assert event.amount() == EUR_40
          assert event.delivery().cost() == Money.ZERO
          assert event.when() == CURRENT_DATE
        })
  }

  def "shouldn't charge for delivery for order value above amount based on promotion price list"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_EUR_40)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> EUR_40

    when:
        def result = paymentHandler.handle(PAY_FOR_ORDER_EUR_40)

    then:
        result.isSuccess()

    and:
        1 * domainEventPublisher.publish({ OrderPaid event ->
          assert event.orderId() == ORDER_ID
          assert event.amount() == EUR_40
          assert event.delivery().cost() == Money.ZERO
          assert event.when() == CURRENT_DATE
        })
  }

  def "should charge for delivery based on price provided by courier system"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_EUR_40)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> EUR_50

    and:
        currentDeliveryCostProvider.currentCost() >> EUR_25

    when:
        def result = paymentHandler.handle(PAY_FOR_ORDER_EUR_40_PLUSEUR_25_DELIVERY)

    then:
        result.isSuccess()

    and:
        1 * domainEventPublisher.publish({ OrderPaid event ->
          assert event.clientId() == CLIENT_ID
          assert event.orderId() == ORDER_ID
          assert event.amount() == EUR_40
          assert event.delivery().cost() == EUR_25
          assert event.when() == CURRENT_DATE
        })
  }

  def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_EUR_40)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> EUR_50

    and:
        currentDeliveryCostProvider.currentCost() >> { throw new RuntimeException() }

    when:
        def result = paymentHandler.handle(PAY_FOR_ORDER_EUR_40_PLUS_20_EUR_DELIVERY)

    then:
        result.isSuccess()

    and:
        1 * domainEventPublisher.publish({ OrderPaid event ->
          assert event.orderId() == ORDER_ID
          assert event.amount() == EUR_40
          assert event.delivery().cost() == EUR_20
          assert event.when() == CURRENT_DATE
        })
  }

  def "shouldn't charge for a previously paid order"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(PAID_ORDER)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> EUR_40

    when:
        def result = paymentHandler.handle(PAY_FOR_ORDER_EUR_40)

    then:
        result.isError()

    and:
        0 * domainEventPublisher.publish(_)
  }

  def "shouldn't accept payment if the amounts differ"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_EUR_40)

    and:
        clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> EUR_40

    when:
        def result = paymentHandler.handle(new PayOrderCommand(ORDER_ID, EUR_50))

    then:
        result.isError()

    and:
        0 * domainEventPublisher.publish(_)
  }
}
