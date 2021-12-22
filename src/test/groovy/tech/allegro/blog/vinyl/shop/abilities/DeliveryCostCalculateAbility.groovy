package tech.allegro.blog.vinyl.shop.abilities

import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostProvider
import tech.allegro.blog.vinyl.shop.delivery.domain.InMemoryDeliveryCostProvider
import tech.allegro.blog.vinyl.shop.delivery.domain.InMemorySpecialPriceProvider
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

trait DeliveryCostCalculateAbility {

  static final DeliveryCostProvider deliveryCostProvider = new InMemoryDeliveryCostProvider()
  static final SpecialPriceProvider specialPriceProvider = new InMemorySpecialPriceProvider()
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

  void cleanup() {
    specialPriceProvider.set(Money.of(80.00, "EUR"))
  }
}
