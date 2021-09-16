package tech.allegro.blog.vinyl.shop.sales.domain

import groovy.transform.PackageScope
import tech.allegro.blog.vinyl.shop.common.money.Money

@PackageScope
class InMemorySpecialPriceProvider implements SpecialPriceProvider {

    private Money mov

    @Override
    Money getMinimumOrderValueForFreeDelivery() {
        return mov
    }

    void set(Money value) {
        this.mov = value
    }
}
