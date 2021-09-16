package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class OrderAggregateBuilder {
    String id
    String clientId
    boolean unpaid = false
    BigDecimal deliveryCost = 40.00
    String currency = "EUR"
    List<Item> items = [new Item(productId: VinylId.of("PRODUCT_001"), price: 40.00)]

    static OrderAggregateBuilder anOrder() {
        return new OrderAggregateBuilder()
    }

    static OrderAggregateBuilder anUnpaidOrder() {
        return new OrderAggregateBuilder().withUnpaid(true)
    }

    @Immutable
    static class Item {
        String productId
        BigDecimal price
    }

    Order build() {
        List<OrderLine> lines = items.collect {
            OrderLine.of(
                    VinylId.of(it.productId),
                    toMoney(it.price, currency)
            )
        }
        return new Order(
                OrderId.of(id),
                ClientId.of(clientId),
                Order.OrderLines.of(lines),
                new Delivery(toMoney(deliveryCost, currency)),
                unpaid
        )
    }

    private static Money toMoney(BigDecimal amount, String currencyCode) {
        return Money.of(amount, Currency.getInstance(currencyCode));
    }
}
