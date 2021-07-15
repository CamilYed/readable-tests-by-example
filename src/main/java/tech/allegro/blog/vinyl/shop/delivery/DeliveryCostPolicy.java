package tech.allegro.blog.vinyl.shop.delivery;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.client.ClientReputation;
import tech.allegro.blog.vinyl.shop.common.commands.Result;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery.StandardDelivery;
import tech.allegro.blog.vinyl.shop.promotion.PromotionPriceCatalogue;

public interface DeliveryCostPolicy {

  Delivery calculate(Money orderValue, ClientReputation clientReputation);
}

@RequiredArgsConstructor
class DefaultDeliveryCostPolicy implements DeliveryCostPolicy {

  private final CurrentDeliveryCostProvider deliveryCostProvider;
  private final PromotionPriceCatalogue promotionPriceCatalogue;
  private final StandardDelivery defaultDelivery = Delivery.standardDeliveryWithDefaultPrice();

  @Override
  public Delivery calculate(Money orderValue, ClientReputation clientReputation) {
    if (clientReputation == ClientReputation.VIP)
      return Delivery.freeDeliveryDueToClientReputation();
    var MOV = promotionPriceCatalogue.freeDeliveryPromotionOrderMinimumValue();
    if (orderValue.greaterThan(MOV))
      return Delivery.freeDeliveryDueToOrderCost();
    final var resultOfGettingCurrentDeliveryCost = Result.run(() -> Delivery.standardDelivery(deliveryCostProvider.currentCost()));
    return resultOfGettingCurrentDeliveryCost.getSuccessOrDefault(defaultDelivery);
  }
}
