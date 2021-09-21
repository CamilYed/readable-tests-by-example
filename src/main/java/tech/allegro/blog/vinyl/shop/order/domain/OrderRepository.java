package tech.allegro.blog.vinyl.shop.order.domain;


import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  default OrderId nextId() {
    return OrderId.of(UUID.randomUUID().toString());
  }

  Optional<Order> findBy(OrderId orderId);

  void save(Order order);
}
