package tech.allegro.blog.vinyl.shop.builders.order


import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.ItemJsonBuilder.anItem

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CreateOrderWithIdJsonBuilder {
    String orderId = TestData.ORDER_ID
    String clientId = TestData.CLIENT_ID
    List<ItemJsonBuilder> items = [anItem().withProductId(TestData.CZESLAW_NIEMEN_ALBUM_ID)
                                           .withCost(euro(40.00))]

    static CreateOrderWithIdJsonBuilder anOrder() {
        return new CreateOrderWithIdJsonBuilder()
    }

    CreateOrderWithIdJsonBuilder withItem(ItemJsonBuilder anItem) {
        items << anItem
        return this
    }

    CreateOrderWithIdJsonBuilder withAmount(MoneyJsonBuilder anAmount) {
        items = [anItem().withProductId(TestData.CZESLAW_NIEMEN_ALBUM_ID)
                         .withCost(anAmount)]
        return this
    }

    Map toMap() {
        return [
                "orderId" : orderId,
                "clientId": clientId,
                "items"   : items.collect { it.toMap() }
        ]
    }

    @Builder(builderStrategy = SimpleStrategy, prefix = "with")
    static class ItemJsonBuilder {
        String productId = TestData.CZESLAW_NIEMEN_ALBUM_ID
        MoneyJsonBuilder cost

        static ItemJsonBuilder anItem() {
            return new ItemJsonBuilder(productId: "productId", cost: [
                    "amount"  : 40.00,
                    "currency": "EUR"
            ])
        }

        Map toMap() {
            return [
                    "productId": productId,
                    "cost"     : cost
            ]
        }
    }
}
