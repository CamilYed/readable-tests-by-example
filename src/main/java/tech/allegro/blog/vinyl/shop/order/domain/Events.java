package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.events.DomainEvent;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.time.Instant;

public class Events {

  public sealed interface OrderDomainEvent extends DomainEvent {
  }

  public static record OrderPaid(
    ClientId clientId,
    OrderId orderId,
    Instant when,
    Money amount,
    Delivery delivery
  ) implements OrderDomainEvent {
  }

  public static record ItemQuantityChanged(
    OrderId orderId,
    VinylId vinylId,
    Quantity afterChange
  ) implements OrderDomainEvent {
  }

  public static record ItemQuantityChangeFailedBecauseAlreadyPaid(
    OrderId orderId,
    VinylId vinylId
  ) implements OrderDomainEvent {
  }

  public static record ItemQuantityChangeFailedBecauseNotExists(
    OrderId orderId,
    VinylId notExistingItem
  ) implements OrderDomainEvent {
  }

  public static record OrderPayFailedBecauseAlreadyPaid(
    OrderId orderId,
    Instant when
  ) implements OrderDomainEvent {
  }

  public static record OrderPayFailedBecauseDifferentAmounts(
    OrderId orderId,
    Instant when,
    Money difference
  ) implements OrderDomainEvent {
  }
}
