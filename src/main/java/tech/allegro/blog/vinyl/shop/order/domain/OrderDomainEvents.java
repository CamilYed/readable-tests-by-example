package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.events.Event;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.time.Instant;

public class OrderDomainEvents {
  public sealed interface OrderDomainEvent extends Event.DomainEvent {
  }

  public sealed interface OrderFailureEvent extends Event.FailureEvent {
  }

  public static record OrderPaid(
    ClientId clientId,
    OrderId orderId,
    Instant when,
    Money amount,
    Delivery delivery
  ) implements OrderDomainEvent {
  }


  public static record OrderPayFailedBecauseAlreadyPaid(
    OrderId orderId,
    Instant when
  ) implements OrderFailureEvent {
  }

  public static record OrderPayFailedBecauseAmountIsDifferent(
    OrderId orderId,
    Instant when
  ) implements OrderFailureEvent {
  }
}


