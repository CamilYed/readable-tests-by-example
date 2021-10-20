package tech.allegro.blog.vinyl.shop.builders.order


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder.anItem

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CreateOrderJsonBuilder {
  String orderId = TestData.ORDER_ID
  String clientId = TestData.CLIENT_ID
  List<ItemJsonBuilder> items = [anItem().withProductId(TestData.CZESLAW_NIEMEN_ALBUM_ID).withUnitPrice(euro(40.00))]

  static CreateOrderJsonBuilder anOrder() {
    return new CreateOrderJsonBuilder()
  }

  CreateOrderJsonBuilder withItem(ItemJsonBuilder anItem) {
    def item = items.find {it.productId == anItem.productId}
    if (item != null) {
      items.remove(item)
      items << anItem
    } else {
      items << anItem
    }
    return this
  }

  CreateOrderJsonBuilder withAmount(MoneyJsonBuilder anAmount) {
    items = [anItem().withProductId(TestData.CZESLAW_NIEMEN_ALBUM_ID).withUnitPrice(anAmount)]
    return this
  }

  Map toMap() {
    return [
      clientId: clientId,
      items   : items.collect { it.toMap() }
    ]
  }
}
