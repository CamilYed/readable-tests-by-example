package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;

import java.time.Instant;

public sealed interface DomainEvent {


  record OrderPaidEvent(
    OrderId orderId,
    Instant when,
    Money amount,
    Delivery delivery
  ) implements DomainEvent {

    OrderPaidEvent(OrderId orderId, Money amount, Delivery delivery) {
      this(orderId, Instant.now(), amount, delivery);
    }

    static OrderPaidEvent of(OrderId id, Money amount, Delivery delivery) {
      return new OrderPaidEvent(id, amount, delivery);
    }
  }
}


