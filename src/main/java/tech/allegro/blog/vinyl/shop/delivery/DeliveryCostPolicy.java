package tech.allegro.blog.vinyl.shop.delivery;

import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.client.ClientReputation;
import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface DeliveryCostPolicy {

  Delivery calculate(Money orderValue, ClientReputation clientReputation);
}


@AllArgsConstructor
class DefaultDeliveryCostPolicy implements DeliveryCostPolicy {

  private Money FREE_DELIVERY_AMOUNT_THRESHOLD;

  @Override
  public Delivery calculate(Money orderValue, ClientReputation clientReputation) {
    if (clientReputation == ClientReputation.VIP)
      return Delivery.freeDelivery();
    if (orderValue.greaterThan(FREE_DELIVERY_AMOUNT_THRESHOLD))
      return Delivery.freeDelivery();
    return null;
  }
}
