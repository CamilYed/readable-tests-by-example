package tech.allegro.blog.vinyl.shop.delivery.domain;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation;
import tech.allegro.blog.vinyl.shop.common.commands.Result;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery.StandardDelivery;
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider;

public interface DeliveryCostPolicy {

  Delivery calculate(Money orderValue, ClientReputation clientReputation);

  @RequiredArgsConstructor
  class DefaultDeliveryCostPolicy implements DeliveryCostPolicy {
    private final DeliveryCostProvider deliveryCostProvider;
    private final SpecialPriceProvider specialPriceProvider;
    private final StandardDelivery defaultDelivery = Delivery.standardDeliveryWithDefaultPrice();

    @Override
    public Delivery calculate(Money orderValue, ClientReputation clientReputation) {
      if (clientReputation.isVip())
        return Delivery.freeDelivery();
      var MOV = specialPriceProvider.getMinimumOrderValueForFreeDelivery();
      if (orderValue.greaterOrEqualTo(MOV))
        return Delivery.freeDelivery();
      final var resultOfGettingCurrentDeliveryCost = Result.run(() -> Delivery.standardDelivery(deliveryCostProvider.currentCost()));
      return resultOfGettingCurrentDeliveryCost.getSuccessOrDefault(defaultDelivery);
    }
  }
}
