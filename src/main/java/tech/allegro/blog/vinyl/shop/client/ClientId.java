package tech.allegro.blog.vinyl.shop.client;

import lombok.Value;

@Value(staticConstructor = "of")
public class ClientId {
  String value;
}
