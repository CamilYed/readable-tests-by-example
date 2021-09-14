package tech.allegro.blog.vinyl.shop.order.domain

import groovy.transform.CompileStatic
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
    boolean unpaid
    BigDecimal deliveryCost
    String currency
    List<Item> items = []

    static class Item {
        String productId
        BigDecimal price
    }

    Order build() {
        List<OrderLine> lines = items.collect {
            OrderLine.of(
                    VinylId.of(it.productId),
                    Money.of(it.price, Currency.getInstance(currency))
            )
        }
        return new Order(
                OrderId.of(id),
                ClientId.of(clientId),
                Order.OrderLines.of(lines),
                new Delivery(Money.of(deliveryCost, Currency.getInstance(currency))),
                unpaid
        )
    }
}
