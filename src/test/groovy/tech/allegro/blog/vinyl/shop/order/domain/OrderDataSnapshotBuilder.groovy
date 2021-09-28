package tech.allegro.blog.vinyl.shop.order.domain

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

    OrderDataSnapshot build() {
        Money cost = items.entrySet().stream()
                .map(it -> it.key.price() * it.getValue())
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
