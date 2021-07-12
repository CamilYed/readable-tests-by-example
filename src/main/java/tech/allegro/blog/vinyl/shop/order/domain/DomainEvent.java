package tech.allegro.blog.vinyl.shop.order.domain;

import java.time.Instant;

public sealed interface DomainEvent {


  record OrderPaidEvent(
    OrderId orderId,
    Instant when
  ) implements DomainEvent {

    OrderPaidEvent(OrderId orderId) {
      this(orderId, Instant.now());
    }

    static OrderPaidEvent of(OrderId id) {
      return new OrderPaidEvent((id));
    }
  }
}


