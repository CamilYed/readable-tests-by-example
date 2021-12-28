package tech.allegro.blog.vinyl.shop.abilities

import tech.allegro.blog.vinyl.shop.assertions.OrderDataSnapshotAssertion
import tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder
import tech.allegro.blog.vinyl.shop.order.domain.InMemoryOrderRepository
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

trait OrderAbility {

  static final OrderRepository orderRepository = new InMemoryOrderRepository()

  void thereIs(OrderDataSnapshotBuilder anOrder) {
    orderRepository.save(anOrder.build())
  }

  OrderDataSnapshotAssertion assertThatThereIsOrderWithId(String orderId) {
    Optional<OrderDataSnapshot> order = orderRepository.findBy(new OrderId(orderId))
    assert order.isPresent()
    return OrderDataSnapshotAssertion.assertThat(order.get())
  }
}
