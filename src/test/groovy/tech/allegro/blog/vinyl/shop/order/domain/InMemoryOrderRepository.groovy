package tech.allegro.blog.vinyl.shop.order.domain

import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders
import tech.allegro.blog.vinyl.shop.order.application.search.PaidClientOrdersView

import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

import static java.util.stream.Collectors.toList
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class InMemoryOrderRepository implements OrderRepository, FindClientOrders {

    private final Map<OrderId, OrderDataSnapshot> orders = new ConcurrentHashMap<OrderId, OrderDataSnapshot>()

    @Override
    Optional<OrderDataSnapshot> findBy(OrderId orderId) {
        return Optional.ofNullable(orders.get(orderId))
    }

    @Override
    void save(OrderDataSnapshot order) {
        orders.put(order.orderId(), order)
    }

    @Override
    PaidClientOrdersView findPaidOrders(ClientId clientId) {
        final var paidOrdersView = orders.values().stream()
                .filter(onlyPaidOrders())
                .collect(toList());
        return new PaidClientOrdersView(paidOrdersView);
    }

    void clear() {
        orders.clear()
    }

    private static Predicate<OrderDataSnapshot> onlyPaidOrders() {
        return (it) -> !it.unpaid();
    }
}