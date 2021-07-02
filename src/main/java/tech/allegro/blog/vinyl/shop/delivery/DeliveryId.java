package tech.allegro.blog.vinyl.shop.delivery;

import java.util.UUID;

public record DeliveryId(String value) {

  static DeliveryId random() {
    return new DeliveryId(UUID.randomUUID().toString());
  }
}
