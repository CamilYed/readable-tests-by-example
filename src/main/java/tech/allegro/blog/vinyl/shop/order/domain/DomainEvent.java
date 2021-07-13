package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;

import java.time.Instant;

public sealed interface DomainEvent {


  record OrderPaidEvent(
    OrderId orderId,
    Instant when,
    Money amount
  ) implements DomainEvent {

    OrderPaidEvent(OrderId orderId, Money amount) {
      this(orderId, Instant.now(), amount);
    }

    static OrderPaidEvent of(OrderId id, Money amount) {
      return new OrderPaidEvent(id, amount);
    }
  }
}


