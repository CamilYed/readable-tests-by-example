package tech.allegro.blog.vinyl.shop.delivery.adpater;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.delivery.domain.CurrentDeliveryCostProvider;

import java.math.BigDecimal;

@RequiredArgsConstructor
class HttpCurrentDeliveryCostProvider implements CurrentDeliveryCostProvider {

  private final DeliveryCostProviderApiClient apiClient;

  @Override
  public Money currentCost() {
    MoneyJson moneyJson = apiClient.currentCost();
    return toDomain(moneyJson);
  }

  private Money toDomain(MoneyJson moneyJson) {
    return Money.of(new BigDecimal(moneyJson.getValue()));
  }
}
