package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.PackageScope

import java.util.concurrent.ConcurrentHashMap

@PackageScope
class InMemoryOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new ConcurrentHashMap<OrderId, Order>()

    @Override
    Optional<Order> findBy(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId))
    }

    @Override
    void save(Order order) {
        orders.put(order.getOrderId(), order)
    }

    void clear() {
        orders.clear()
    }
}
