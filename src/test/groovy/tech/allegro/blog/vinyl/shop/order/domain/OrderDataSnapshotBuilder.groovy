package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder

import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot.*
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderDataSnapshotBuilder {
    String id = TestData.ORDER_ID
    String clientId = TestData.CLIENT_ID
    boolean unpaid = false
    BigDecimal deliveryCost = 40.00
    String currency = TestData.EURO_CURRENCY_CODE
    List<Item> items = [new Item(TestData.VINYL_CZESLAW_NIEMEN_ID, TestData._40_EUR)]

    static OrderDataSnapshotBuilder aPaidOrder() {
        return new OrderDataSnapshotBuilder()
    }

    static OrderDataSnapshotBuilder anUnpaidOrder() {
        return new OrderDataSnapshotBuilder().withUnpaid(true)
    }

    OrderDataSnapshotBuilder withAmount(MoneyBuilder amount) {
        items = [new Item(TestData.VINYL_CZESLAW_NIEMEN_ID, amount.build())]
        return this
    }

    static class Item {
        VinylId productId
        Money price

        Item(VinylId productId, Money price) {
            this.productId = productId
            this.price = price
        }

        def toViewItem() {
            return OrderDataSnapshot.Item.of(productId, price)
        }
    }

    OrderDataSnapshot build() {
        Money cost = items.stream().map(it -> it.price).reduce(Money.ZERO, Money::add)
        List<OrderDataSnapshot.Item> items = items.stream().map(it -> it.toViewItem()).toList()
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
