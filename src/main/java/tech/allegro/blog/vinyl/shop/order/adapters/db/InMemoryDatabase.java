package tech.allegro.blog.vinyl.shop.order.adapters.db;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders;
import tech.allegro.blog.vinyl.shop.order.application.search.PaidClientOrdersView;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

class InMemoryDatabase implements OrderRepository, FindClientOrders {

  private final Map<OrderId, OrderDataSnapshot> orders = new ConcurrentHashMap<>();

  @Override
  public OrderId nextId() {
    return new OrderId(UUID.randomUUID().toString());
  }

  @Override
  public Optional<OrderDataSnapshot> findBy(OrderId orderId) {
    return Optional.ofNullable(orders.get(orderId));
  }

  @Override
  public void save(OrderDataSnapshot order) {
    orders.put(order.orderId(), order);
  }

  @Override
  public PaidClientOrdersView findPaidOrders(ClientId clientId) {
    final var paidOrdersView = orders.values().stream()
      .filter(onlyPaidOrders())
      .collect(toList());
    return new PaidClientOrdersView(paidOrdersView);
  }

  private static Predicate<OrderDataSnapshot> onlyPaidOrders() {
    return it -> !it.unpaid();
  }
}
