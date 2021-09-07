package tech.allegro.blog.vinyl.shop.delivery.domain;

import tech.allegro.blog.vinyl.shop.common.money.Money;

public interface DeliveryCostProvider {

  Money currentCost();
}
