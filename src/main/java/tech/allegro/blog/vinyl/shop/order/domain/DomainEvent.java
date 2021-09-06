package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.Value;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;

import java.time.Instant;

public interface DomainEvent {

  @Value(staticConstructor = "of")
  class OrderPaid implements DomainEvent {
    ClientId clientId;
    OrderId orderId;
    Instant when;
    Money amount;
    Delivery delivery;
  }

  @Value(staticConstructor = "of")
  class OrderPayFailed implements DomainEvent {
    OrderId orderId;
    Instant when;
    Reason reason;

    public enum Reason {
      ALREADY_PAID,
      AMOUNT_IS_DIFFERENT
    }
  }
}


