package tech.allegro.blog.vinyl.shop.delivery.domain


import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

class InMemorySpecialPriceProvider implements SpecialPriceProvider {

  private Money mov = new Money(80.00, TestData.EURO_CURRENCY)

  @Override
  Money getMinimumOrderValueForFreeDelivery() {
    return mov
  }

  void set(Money value) {
    this.mov = value
  }
}
