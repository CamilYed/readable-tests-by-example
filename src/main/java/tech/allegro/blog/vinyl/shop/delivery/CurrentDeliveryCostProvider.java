package tech.allegro.blog.vinyl.shop.delivery;

import tech.allegro.blog.vinyl.shop.common.money.Money;

interface CurrentDeliveryCostProvider {

  default Money currentCost() {
    return Money.of(22.00);
  }
}
