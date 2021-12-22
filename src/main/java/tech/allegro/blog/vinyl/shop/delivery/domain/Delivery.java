package tech.allegro.blog.vinyl.shop.delivery.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;
// TODO nice to have DeliveryId, and approximate delivery time
public record Delivery(Money cost) {
  public static Delivery freeDelivery() {
    return new Delivery(Money.ZERO);
  }

  public static Delivery standardDelivery(Money cost) {
    return new Delivery(cost);
  }

  public static Delivery standardDeliveryWithDefaultPrice() {
    return new Delivery(Money.of("20.00", "EUR"));
  }
}
