package tech.allegro.blog.vinyl.shop.sales.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface SpecialPriceProvider {

  Money getMinimumOrderValueForFreeDelivery();
}
