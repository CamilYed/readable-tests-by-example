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
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository
import tech.allegro.blog.vinyl.shop.order.domain.SampleOrder
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPaid
import static tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy.DefaultDeliveryCostPolicy
import static tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPayFailed.Reason.ALREADY_PAID
import static tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPayFailed.Reason.AMOUNT_IS_DIFFERENT

class OrderPaymentNotRefactoredSpec extends Specification {

    OrderRepository orderRepository = Stub()
    ClientReputationProvider clientReputationProvider = Stub()
    CurrentDeliveryCostProvider currentDeliveryCostProvider = Stub()
    SpecialPriceProvider specialPriceProvider = Stub()
    DeliveryCostPolicy deliveryCostPolicy = new DefaultDeliveryCostPolicy(currentDeliveryCostProvider, specialPriceProvider)
    DomainEventPublisher domainEventPublisher = Mock()

    @Subject
    OrderPaymentHandler paymentHandler = new OrderPaymentHandler(
        orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher
    )

    final Instant CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")
    final Clock TEST_CLOCK = Clock.fixed(CURRENT_DATE, ZoneId.systemDefault())
    final Money _20_EUR = Money.of("20.00", "EUR")
    final Money _25_EUR = Money.of("25.00", "EUR")
    final Money _40_EUR = Money.of("40.00", "EUR")
    final Money _50_EUR = Money.of("50.00", "EUR")
    final ClientId CLIENT_ID = ClientId.of("1")
    final VinylId PRODUCT_ID = VinylId.of("1")
    final OrderId ORDER_ID = OrderId.of("1")
    final Order UNPAID_ORDER_40_EUR = SampleOrder.build(CLIENT_ID, ORDER_ID, _40_EUR, PRODUCT_ID, true)
    final Order PAID_ORDER = SampleOrder.build(CLIENT_ID, ORDER_ID, _40_EUR, PRODUCT_ID, false)
    final ClientReputation VIP = ClientReputation.vip(CLIENT_ID)
    final ClientReputation NOT_VIP = ClientReputation.notVip(CLIENT_ID)
    final PayOrderCommand PAY_FOR_ORDER_40_EUR = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR)
    final PayOrderCommand PAY_FOR_ORDER_40_EUR_PLUS_20_EUR_DELIVERY = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR.add(_20_EUR))
    final PayOrderCommand PAY_FOR_ORDER_40_EUR_PLUS_25_EUR_DELIVERY = PayOrderCommand.of(CLIENT_ID, ORDER_ID, _40_EUR.add(_25_EUR))

    def setup() {
        ClockProvider.setSystemClock(TEST_CLOCK)
    }

    def "shouldn't charge for delivery when the client has a VIP status"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_40_EUR)

        and:
            clientReputationProvider.get(CLIENT_ID) >> VIP

        when:
            paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

        then:
            noExceptionThrown()

        then:
            1 * domainEventPublisher.publish({ OrderPaid event ->
                assert event.orderId == ORDER_ID
                assert event.amount == _40_EUR
                assert event.delivery.cost == Money.ZERO
                assert event.when == CURRENT_DATE
            })
    }

    def "shouldn't charge for delivery for order value above amount based on promotion price list"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_40_EUR)

        and:
            clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

        and:
            specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _40_EUR

        when:
            paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

        then:
            1 * domainEventPublisher.publish({ OrderPaid event ->
                assert event.orderId == ORDER_ID
                assert event.amount == _40_EUR
                assert event.delivery.cost == Money.ZERO
                assert event.when == CURRENT_DATE
            })
    }

    def "should charge for delivery based on price provided by courier system"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_40_EUR)

        and:
            clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

        and:
            specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _50_EUR

        and:
            currentDeliveryCostProvider.currentCost() >> _25_EUR

        when:
            paymentHandler.handle(PAY_FOR_ORDER_40_EUR_PLUS_25_EUR_DELIVERY)

        then:
            1 * domainEventPublisher.publish({ OrderPaid event ->
                assert event.clientId == CLIENT_ID
                assert event.orderId == ORDER_ID
                assert event.amount == _40_EUR
                assert event.delivery.cost == _25_EUR
                assert event.when == CURRENT_DATE
            })
    }

    def "should charge always 20 euro for delivery when the courier system is unavailable"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_40_EUR)

        and:
            clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

        and:
            specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _50_EUR

        and:
            currentDeliveryCostProvider.currentCost() >> { throw new RuntimeException() }

        when:
            paymentHandler.handle(PAY_FOR_ORDER_40_EUR_PLUS_20_EUR_DELIVERY)

        then:
            1 * domainEventPublisher.publish({ OrderPaid event ->
                assert event.orderId == ORDER_ID
                assert event.amount == _40_EUR
                assert event.delivery.cost == _20_EUR
                assert event.when == CURRENT_DATE
            })
    }

    def "shouldn't charge for a previously paid order"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(PAID_ORDER)

        and:
            clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

        and:
            specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _40_EUR

        when:
            paymentHandler.handle(PAY_FOR_ORDER_40_EUR)

        then:
            1 * domainEventPublisher.publish({ OrderDomainEvents.OrderPayFailed event ->
                assert event.orderId == ORDER_ID
                assert event.when == CURRENT_DATE
                assert event.reason == ALREADY_PAID
            })
    }

    def "shouldn't accept payment if the amounts differ"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(UNPAID_ORDER_40_EUR)

        and:
            clientReputationProvider.get(CLIENT_ID) >> NOT_VIP

        and:
            specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> _40_EUR

        when:
            paymentHandler.handle(PayOrderCommand.of(CLIENT_ID, ORDER_ID, _50_EUR))

        then:
            1 * domainEventPublisher.publish({ OrderDomainEvents.OrderPayFailed event ->
                assert event.orderId == ORDER_ID
                assert event.when == CURRENT_DATE
                assert event.reason == AMOUNT_IS_DIFFERENT
            })
    }
}
