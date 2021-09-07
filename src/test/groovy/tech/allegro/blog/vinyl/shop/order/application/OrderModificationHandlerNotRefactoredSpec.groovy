package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository
import tech.allegro.blog.vinyl.shop.order.domain.SampleOrder

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Order.CanNotModifyPaidOrder

class OrderModificationHandlerNotRefactoredSpec extends Specification {

  OrderRepository orderRepository = Stub()

  @Subject
  OrderModificationHandler orderModificationHandler = new OrderModificationHandler(orderRepository)

  static final ClientId CLIENT_ID = ClientId.of("1")
  static final OrderId ORDER_ID = OrderId.of("1")
  final Money _40_EUR = Money.of("40.00","EUR")
  final VinylId PRODUCT_ID = VinylId.of("1")
  final Order PAID_ORDER = SampleOrder.build(CLIENT_ID, ORDER_ID, _40_EUR, PRODUCT_ID, false)
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
