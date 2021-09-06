package tech.allegro.blog.vinyl.shop.order.adapters.db;

import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class ImMemoryDatabase implements OrderRepository  {

  private final Map<OrderId, Order> orders = new ConcurrentHashMap<>();

  @Override
  public OrderId nextId() {
    return OrderId.of(UUID.randomUUID().toString());
  }

  @Override
  public Optional<Order> findBy(OrderId orderId) {
    return Optional.ofNullable(orders.get(orderId));
  }

  @Override
  public void save(Order order) {
    orders.putIfAbsent(order.getOrderId(), order);
  }
}
