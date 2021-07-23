package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class OrderId {
  String value;
}
