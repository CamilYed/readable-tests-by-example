package tech.allegro.blog.vinyl.shop.promotion;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface PromotionPriceCatalogue {
  default Money freeDeliveryPromotionOrderMinimumValue() {
    return Money.of("100.00");
  }
}
