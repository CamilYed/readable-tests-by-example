package tech.allegro.blog.vinyl.shop.delivery.domain


import tech.allegro.blog.vinyl.shop.common.money.Money

class InMemoryDeliveryCostProvider implements DeliveryCostProvider {

    private Money deliveryCost = Money.of("20.00", "EUR")
    private boolean simulateUnavailability = false

    @Override
    Money currentCost() {
        if (simulateUnavailability )
            throw new RuntimeException("Any error")
        return deliveryCost
    }

    void set(Money cost) {
        this.deliveryCost = cost
    }

    void simulateUnavailability() {
        simulateUnavailability = true
    }
}