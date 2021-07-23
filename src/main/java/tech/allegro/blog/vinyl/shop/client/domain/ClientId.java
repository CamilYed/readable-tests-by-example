package tech.allegro.blog.vinyl.shop.client.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class ClientId {
  String value;
}
