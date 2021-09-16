package tech.allegro.blog.vinyl.shop.sales.domain

import tech.allegro.blog.vinyl.shop.common.money.Money

trait GetMinimumOrderDeliveryValueForFreeDeliveryAbility {

    final SpecialPriceProvider specialPriceProvider = new InMemorySpecialPriceProvider()

    void minimumOrderValueForFeeDeliveryIs(Money value) {
        specialPriceProvider.set(value)
    }
}