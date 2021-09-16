package tech.allegro.blog.vinyl.shop.order.application.notrefactored

import org.apache.groovy.util.Maps
import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Order.CanNotModifyPaidOrder

class OrderModificationHandlerNotRefactoredSpec extends Specification {

    OrderRepository orderRepository = Stub()

    @Subject
    OrderModificationHandler orderModificationHandler = new OrderModificationHandler(orderRepository)
    OrderFactory orderFactory = new OrderFactory()

    static final ClientId CLIENT_ID = ClientId.of("1")
    static final OrderId ORDER_ID = OrderId.of("1")
    final Money _40_EUR = Money.of("40.00", "EUR")
    final VinylId PRODUCT_ID = VinylId.of("1")
    final Order PAID_ORDER = orderFactory.create(ORDER_ID, CLIENT_ID, Maps.of(PRODUCT_ID, _40_EUR), false)
    static final List<OrderModificationHandler.Item> ITEMS = [OrderModificationHandler.Item.of(VinylId.of("2"), Money.of("23.00", "EUR"))]

    def "shouldn't modify paid order"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(PAID_ORDER)

        when:
            orderModificationHandler.handle(AddItemsToOrderCommand.of(ORDER_ID, ITEMS))

        then:
            thrown(CanNotModifyPaidOrder)
    }
}
