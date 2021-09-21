package tech.allegro.blog.vinyl.shop.order.domain

import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders
import tech.allegro.blog.vinyl.shop.order.application.search.PaidClientOrdersView

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

import static java.util.stream.Collectors.toList
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class InMemoryOrderRepository implements OrderRepository, FindClientOrders {

    private final Map<OrderId, Order> orders = new ConcurrentHashMap<OrderId, Order>()

    @Override
    Optional<Order> findBy(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId))
    }

    @Override
    void save(Order order) {
        orders.put(order.getOrderId(), order)
    }

    @Override
    PaidClientOrdersView findPaidOrders(ClientId clientId) {
        final var paidOrdersView = orders.values().stream()
                .map(Order::toSnapshot)
                .filter(onlyPaidOrders())
                .collect(toList());
        return PaidClientOrdersView.of(paidOrdersView);
    }

    void clear() {
        orders.clear()
    }

    private static Predicate<Values.OrderDataSnapshot> onlyPaidOrders() {
        return (it) -> !it.isUnpaid();
    }
}