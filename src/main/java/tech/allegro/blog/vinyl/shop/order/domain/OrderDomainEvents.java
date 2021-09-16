package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.Value;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.events.DomainEvent;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;

import java.time.Instant;

public class OrderDomainEvents {

  @Value(staticConstructor = "of")
  public static class OrderPaid implements DomainEvent {
    ClientId clientId;
    OrderId orderId;
    Instant when;
    Money amount;
    Delivery delivery; // Could be a Money
  }

  @Value(staticConstructor = "of")
  public static class OrderPayFailed implements DomainEvent {
    OrderId orderId;
    Instant when;
    Reason reason;

    public enum Reason {
      ALREADY_PAID,
      AMOUNT_IS_DIFFERENT
    }
  }

  @Value(staticConstructor = "of")
  public static class ProductAddedToOrder implements DomainEvent {
    OrderId orderId;
    VinylId productId;
    Instant when;
  }

  @Value(staticConstructor = "of")
  public static class ProductRemovedFromOrder implements DomainEvent {
    OrderId orderId;
    VinylId productId;
    Instant when;
  }
}


