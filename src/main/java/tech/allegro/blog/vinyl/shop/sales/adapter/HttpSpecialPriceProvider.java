package tech.allegro.blog.vinyl.shop.sales.adapter;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider;

@RequiredArgsConstructor
class HttpSpecialPriceProvider implements SpecialPriceProvider {

  private final SpecialPriceCatalogueApiClient apiClient;

  @Override
  public Money getMinimumOrderValueForFreeDelivery() {
    MoneyJson moneyJson = apiClient.getSpecialPrice(SPECIAL_OFFER_ID);
    return toDomain(moneyJson);
  }

  private static Money toDomain(MoneyJson json) {
    return Money.of(json.getAmount(), json.getCurrency());
  }

  private static final String SPECIAL_OFFER_ID = "FREE_DELIVERY_ORDER_PRICE_ID";
}
