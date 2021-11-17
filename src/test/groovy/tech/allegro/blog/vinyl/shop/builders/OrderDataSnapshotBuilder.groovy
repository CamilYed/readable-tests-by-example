package tech.allegro.blog.vinyl.shop.builders

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.common.volume.Quantity

import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderDataSnapshotBuilder {
  String id = TestData.ORDER_ID
  String clientId = TestData.CLIENT_ID
  boolean unpaid = false
  BigDecimal deliveryCost = 40.00
  String currency = TestData.EURO_CURRENCY_CODE
  Map<Vinyl, Quantity> items = [(TestData.VINYL_CZESLAW_NIEMEN): Quantity.ONE]

  static OrderDataSnapshotBuilder aPaidOrder() {
    return new OrderDataSnapshotBuilder()
  }

  static OrderDataSnapshotBuilder anUnpaidOrder() {
    return new OrderDataSnapshotBuilder().withUnpaid(true)
  }

  OrderDataSnapshotBuilder withAmount(MoneyBuilder amount) {
    items = [(new Vinyl(TestData.VINYL_CZESLAW_NIEMEN_ID, amount.build())): Quantity.ONE]
    return this
  }

  OrderDataSnapshotBuilder withItem(ItemBuilder anItem) {
    items.removeAll { it -> it.key.vinylId().value() == anItem.productId }
    items << anItem.build()
    return this
  }

  OrderDataSnapshotBuilder withItems(ItemBuilder... anItems) {
    items.clear()
    anItems.each {
      items << it.build()
    }
    return this
  }

  @Builder(builderStrategy = SimpleStrategy, prefix = "with")
  static class ItemBuilder {
    String productId = TestData.VINYL_CZESLAW_NIEMEN_ID
    Money unitPrice = TestData.EUR_40
    int quantity = 1

    static ItemBuilder anItem() {
      return new ItemBuilder()
    }

    ItemBuilder withUnitPrice(MoneyBuilder anUnitPrice) {
      unitPrice = anUnitPrice.build()
      return this
    }

    Map<Vinyl, Quantity> build() {
      return [(new Vinyl(new VinylId(productId), unitPrice)): new Quantity(quantity)]
    }
  }

  OrderDataSnapshot build() {
    Money cost = items.entrySet().stream()
      .map(it -> it.key.unitPrice() * it.getValue())
      .reduce(Money.ZERO, Money::add)
    return new OrderDataSnapshot(
      new ClientId(clientId),
      new OrderId(id),
      cost,
      new Money(deliveryCost, Currency.getInstance(currency)),
      items,
      unpaid
    )
  }
}
