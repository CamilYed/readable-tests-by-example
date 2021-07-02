package tech.allegro.blog.vinyl.shop.delivery;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public record Delivery(
  DeliveryId id,
  Money cost
) {

  public static Delivery defaultPrice() {
    return new Delivery(DeliveryId.random(), new Money(20.00));
  }

  public static Delivery freeDelivery() {
    return new Delivery(DeliveryId.random(), new Money(00.00));
  }
}
