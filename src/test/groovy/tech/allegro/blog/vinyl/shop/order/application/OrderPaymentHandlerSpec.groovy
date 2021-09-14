package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.stubs.FakeOrderRepository

abstract class OrderPaymentHandlerBaseSpec extends Specification {

    private FakeOrderRepository orderRepository = new FakeOrderRepository()
    OrderPaymentHandler paymentHandler = new OrderHandlersConfig()
            .orderPaymentHandler(orderRepository, )


}

class OrderPaymentHandlerSpec extends OrderPaymentHandlerBaseSpec {


    def "shouldn't charge for delivery when the client has a VIP status"() {

    }

    def "shouldn't charge for delivery for order value above amount based on promotion price list"() {

    }

    def "should charge for delivery based on price provided by courier system"() {

    }

    def "shouldn't charge for a previously paid order"() {

    }

    def "should charge always 20 euro for delivery when the courier system is unavailable"() {

    }

    def "shouldn't accept payment if the amounts differ"() {

    }
}
