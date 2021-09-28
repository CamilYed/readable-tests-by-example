package tech.allegro.blog.vinyl.shop.catalogue.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public record Vinyl(
  VinylId vinylId,
  Money price
) {
}
