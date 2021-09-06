package tech.allegro.blog.vinyl.shop.order.domain;


import java.util.Optional;

public interface OrderRepository {

  OrderId nextId();

  Optional<Order> findBy(OrderId orderId);

  void save(Order order);
}
