package tech.allegro.blog.vinyl.shop.order.domain;


import io.vavr.control.Option;

public interface OrderRepository {

  Option<Order> findBy(OrderId orderId);
}
