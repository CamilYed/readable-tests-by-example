package tech.allegro.blog.vinyl.shop.order.domain

trait AddOrderAbility {

    final OrderRepository orderRepository = new InMemoryOrderRepository()

    void thereIs(OrderDataSnapshotBuilder anOrder) {
        orderRepository.save(anOrder.build())
    }

    void clear() {
        orderRepository.clear()
    }
}