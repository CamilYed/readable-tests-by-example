package tech.allegro.blog.vinyl.shop.delivery;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.client.ClientReputation;
import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface DeliveryCostPolicy {

  Delivery calculate(Money orderValue, ClientReputation clientReputation);
}

@RequiredArgsConstructor
class DefaultDeliveryCostPolicy implements DeliveryCostPolicy {

  private final CurrentDeliveryCostProvider deliveryCostProvider;
  private final Money FREE_DELIVERY_AMOUNT_THRESHOLD;

  @Override
  public Delivery calculate(Money orderValue, ClientReputation clientReputation) {
    if (clientReputation == ClientReputation.VIP)
      return Delivery.freeDelivery();
    if (orderValue.greaterThan(FREE_DELIVERY_AMOUNT_THRESHOLD))
      return Delivery.freeDelivery();
    return Try.of(deliveryCostProvider::currentCost)
      .map(Delivery::of)
      .getOrElseGet(it -> Delivery.defaultPrice());
  }
}
