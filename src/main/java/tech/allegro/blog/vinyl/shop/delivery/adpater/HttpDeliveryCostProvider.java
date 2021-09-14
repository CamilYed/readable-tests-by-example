package tech.allegro.blog.vinyl.shop.delivery.adpater;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostProvider;

@RequiredArgsConstructor
class HttpDeliveryCostProvider implements DeliveryCostProvider {
  private final DeliveryCostServiceApiClient apiClient;

  @Override
  public Money currentCost() {
    MoneyJson moneyJson = apiClient.currentCost();
    return toDomain(moneyJson);
  }

  private Money toDomain(MoneyJson moneyJson) {
    return Money.of(moneyJson.getAmount(), moneyJson.getCurrency());
  }
}
