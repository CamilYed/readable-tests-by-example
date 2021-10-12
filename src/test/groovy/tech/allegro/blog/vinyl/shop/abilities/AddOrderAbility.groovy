package tech.allegro.blog.vinyl.shop.abilities

import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler
import tech.allegro.blog.vinyl.shop.order.domain.InMemoryOrderRepository
import tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

trait AddOrderAbility {

  final OrderRepository orderRepository = new InMemoryOrderRepository()

  void thereIs(OrderDataSnapshotBuilder anOrder) {
    orderRepository.save(anOrder.build())
  }

  void clearOrders() {
    orderRepository.clear()
  }
}
