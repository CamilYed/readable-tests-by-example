package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

import static tech.allegro.blog.vinyl.shop.order.domain.Values.*

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderAggregateBuilder {
    String id = TestData.ORDER_ID
    String clientId = TestData.CLIENT_ID
    boolean unpaid = false
    BigDecimal deliveryCost = 40.00
    String currency = TestData.EURO_CURRENCY_CODE
    List<Item> items = [new Item(TestData.VINYL_CZESLAW_NIEMEN_ID, TestData._40_EUR)]

    static OrderAggregateBuilder aPaidOrder() {
        return new OrderAggregateBuilder()
    }

    static OrderAggregateBuilder anUnpaidOrder() {
        return new OrderAggregateBuilder().withUnpaid(true)
    }

    OrderAggregateBuilder withAmount(MoneyBuilder amount) {
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
    }

    Order build() {
        List<OrderLine> lines = items.collect {
            OrderLine.of(
                    it.productId,
                    it.price
            )
        }
        return new Order(
                OrderId.of(id),
                ClientId.of(clientId),
                OrderLines.of(lines),
                Delivery.of(toMoney(deliveryCost, currency)),
                unpaid
        )
    }

    private static Money toMoney(BigDecimal amount, String currencyCode) {
        return Money.of(amount, Currency.getInstance(currencyCode));
    }
}
