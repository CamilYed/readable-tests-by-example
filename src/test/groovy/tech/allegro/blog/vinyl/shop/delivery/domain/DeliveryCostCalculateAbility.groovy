package tech.allegro.blog.vinyl.shop.delivery.domain


import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

trait DeliveryCostCalculateAbility {

    final DeliveryCostProvider deliveryCostProvider = new InMemoryDeliveryCostProvider()
    final SpecialPriceProvider specialPriceProvider = new InMemorySpecialPriceProvider()
    final DeliveryCostPolicy deliveryCostPolicy = new DeliveryCostPolicy.DefaultDeliveryCostPolicy(deliveryCostProvider, specialPriceProvider)

    void currentDeliveryCostIs(MoneyBuilder cost) {
        deliveryCostProvider.set(cost.build())
    }

    void minimumOrderValueForFreeDeliveryIs(MoneyBuilder anAmount) {
        specialPriceProvider.set(anAmount.build())
    }

    void externalCourierSystemIsUnavailable() {
        deliveryCostProvider.simulateUnavailability()
    }
}
