package tech.allegro.blog.vinyl.shop.delivery;

import tech.allegro.blog.vinyl.shop.common.money.Money;

interface CurrentDeliveryCostProvider {

  default Money currentCost() {
    return new Money(22.00);
  }
}
