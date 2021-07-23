package tech.allegro.blog.vinyl.shop.catalogue;

import lombok.Value;

@Value(staticConstructor = "of")
public class VinylId {
  String value;
}
