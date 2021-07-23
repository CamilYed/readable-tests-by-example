package tech.allegro.blog.vinyl.shop.delivery;

import lombok.Value;

import java.util.UUID;

@Value(staticConstructor = "of")
public class DeliveryId {
  String value;

  static DeliveryId random() {
    return new DeliveryId(UUID.randomUUID().toString());
  }
}
