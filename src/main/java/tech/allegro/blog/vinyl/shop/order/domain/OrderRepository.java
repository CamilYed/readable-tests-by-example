package tech.allegro.blog.vinyl.shop.order.domain;


import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  default OrderId nextId() {
    return new OrderId(UUID.randomUUID().toString());
  }

  Optional<OrderDataSnapshot> findBy(OrderId orderId);

  void save(OrderDataSnapshot order);
}
