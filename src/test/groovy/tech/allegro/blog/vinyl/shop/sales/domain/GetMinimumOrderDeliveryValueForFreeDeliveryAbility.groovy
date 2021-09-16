package tech.allegro.blog.vinyl.shop.sales.domain


import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder

trait GetMinimumOrderDeliveryValueForFreeDeliveryAbility {

    static final SpecialPriceProvider specialPriceProvider = new InMemorySpecialPriceProvider()

    void minimumOrderValueForFreeDeliveryIs(MoneyBuilder anAmount) {
        specialPriceProvider.set(anAmount.build())
    }

    void minimumOrderValueForFreeDeliveryIs(Money value) {
        specialPriceProvider.set(value)
    }
}