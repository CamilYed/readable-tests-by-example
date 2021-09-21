package tech.allegro.blog.vinyl.shop.order.domain

trait AddOrderAbility {

    final OrderRepository orderRepository = new InMemoryOrderRepository()

    void thereIs(OrderAggregateBuilder anOrder) {
        orderRepository.save()
    }

    void clear() {
        orderRepository.clear()
    }
}