package tech.allegro.blog.vinyl.shop.delivery.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface CurrentDeliveryCostProvider {

  default Money currentCost() {
    return Money.of("22.00");
  }
}
