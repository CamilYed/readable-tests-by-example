package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.Value;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;

import java.time.Instant;

public interface DomainEvent {

  @Value(staticConstructor = "of")
  class OrderPaidEvent implements DomainEvent {
    OrderId orderId;
    Instant when;
    Money amount;
    Delivery delivery;
  }
}


