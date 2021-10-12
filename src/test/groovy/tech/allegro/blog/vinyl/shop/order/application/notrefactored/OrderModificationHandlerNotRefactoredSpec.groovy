package tech.allegro.blog.vinyl.shop.order.application.notrefactored


import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.volume.Quantity
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.ChangeItemQuantityCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class OrderModificationHandlerNotRefactoredSpec extends Specification {

  OrderRepository orderRepository = Stub()

  @Subject
  OrderModificationHandler orderModificationHandler = new OrderModificationHandler(orderRepository, new OrderFactory())

  static final ClientId CLIENT_ID = new ClientId("1")
  static final OrderId ORDER_ID = new OrderId("1")
  static final Money EUR_40 = Money.of("40.00", "EUR")
  static final Vinyl VINYL_1 = new Vinyl(new VinylId("1"), Money.of("23.00", "EUR"))
  static final Map<Vinyl, Quantity> ITEMS = [(VINYL_1): new Quantity(1)]
  final OrderDataSnapshot PAID_ORDER = new OrderDataSnapshot(CLIENT_ID, ORDER_ID, EUR_40, EUR_40, ITEMS, false)

  def "shouldn't modify paid order"() {
    given:
        orderRepository.findBy(ORDER_ID) >> Optional.of(PAID_ORDER)

    when:
        def result = orderModificationHandler.handle(new ChangeItemQuantityCommand(ORDER_ID, new VinylId("1"), new QuantityChange(1)))

    then:
        result.isError()
  }
}
