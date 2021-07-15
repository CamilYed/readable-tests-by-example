package tech.allegro.blog.vinyl.shop.promotion;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface PromotionPriceCatalogue {
  default Money freeDeliveryPromotionOrderMinimumValue() {
    return new Money(100.00);
  }
}
