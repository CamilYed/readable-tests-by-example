package tech.allegro.blog.vinyl.shop.delivery.domain

import groovy.transform.PackageScope
import tech.allegro.blog.vinyl.shop.common.money.Money

@PackageScope
class InMemoryDeliveryCostProvider implements DeliveryCostProvider {

    private Money deliveryCost = Money.of("20.00", "EUR")

    @Override
    Money currentCost() {
        return deliveryCost
    }

    void set(Money cost) {
        this.deliveryCost = cost
    }
}