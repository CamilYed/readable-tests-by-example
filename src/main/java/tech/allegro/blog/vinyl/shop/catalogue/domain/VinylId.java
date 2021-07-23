package tech.allegro.blog.vinyl.shop.catalogue.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class VinylId {
  String value;
}
