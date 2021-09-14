package tech.allegro.blog.vinyl.shop.stubs

import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderAggregateBuilder
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import java.util.concurrent.ConcurrentHashMap

class FakeOrderRepository implements OrderRepository {

    private final Map<OrderId, Order> orders = new ConcurrentHashMap<OrderId, Order>()

    void thereIsAn(OrderAggregateBuilder anOrder) {
        save(anOrder.build())
    }


    @Override
    Optional<Order> findBy(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId))
    }

    @Override
    void save(Order order) {
        orders.put(order.getOrderId(), order)
    }
}
