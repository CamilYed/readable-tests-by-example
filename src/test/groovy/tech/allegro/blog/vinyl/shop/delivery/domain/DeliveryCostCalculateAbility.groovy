package tech.allegro.blog.vinyl.shop.delivery.domain

import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.sales.domain.GetMinimumOrderDeliveryValueForFreeDeliveryAbility

trait DeliveryCostCalculateAbility implements GetMinimumOrderDeliveryValueForFreeDeliveryAbility {

    final DeliveryCostProvider deliveryCostProvider = new InMemoryDeliveryCostProvider()
    final DeliveryCostPolicy deliveryCostPolicy = new DeliveryCostPolicy.DefaultDeliveryCostPolicy(deliveryCostProvider, specialPriceProvider)

    void currentDeliveryCostIs(Money cost) {
        deliveryCostProvider.set(cost)
    }

    Delivery calculate(Money orderValue, ClientReputation clientReputation) {
        return deliveryCostPolicy.calculate(orderValue, clientReputation)
    }
}
