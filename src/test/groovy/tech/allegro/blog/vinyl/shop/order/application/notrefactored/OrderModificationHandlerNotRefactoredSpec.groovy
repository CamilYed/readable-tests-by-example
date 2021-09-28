package tech.allegro.blog.vinyl.shop.order.application.notrefactored


import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.volume.Quantity
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class OrderModificationHandlerNotRefactoredSpec extends Specification {

    OrderRepository orderRepository = Stub()

    @Subject
    OrderModificationHandler orderModificationHandler = new OrderModificationHandler(orderRepository, new OrderFactory())

    static final ClientId CLIENT_ID = new ClientId("1")
    static final OrderId ORDER_ID = new OrderId("1")
    static final Money _40_EUR = Money.of("40.00", "EUR")
    static final Vinyl VINYL_1 = new Vinyl(new VinylId("1"), Money.of("23.00", "EUR"))
    static final Vinyl VINYL_2 = new Vinyl(new VinylId("2"), Money.of("42.00", "EUR"))
    static final Map<Vinyl, Quantity> ITEMS = [(VINYL_1): new Quantity(1)]
    static final Map<Vinyl, Quantity> ITEMS_TO_ADD = [(VINYL_2): new Quantity(1)]
    final OrderDataSnapshot PAID_ORDER = new OrderDataSnapshot(CLIENT_ID, ORDER_ID, _40_EUR, _40_EUR, ITEMS, false)

    def "shouldn't modify paid order"() {
        given:
            orderRepository.findBy(ORDER_ID) >> Optional.of(PAID_ORDER)

        when:
            orderModificationHandler.handle(new AddItemsToOrderCommand(ORDER_ID, ITEMS_TO_ADD))

        then:
            thrown(Order.CanNotModifyPaidOrder)
    }
}
