package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;

import java.util.Map;
import java.util.stream.Collectors;

public class OrderFactory {

  public Order createUnpaidOrder(OrderId orderId, ClientId clientId, Map<VinylId, Money> items) {
    var lines = items.entrySet().stream()
      .map(it -> OrderLine.create(it.getKey(), it.getValue()))
      .collect(Collectors.toList());
    return new Order(orderId, clientId, Order.OrderLines.of(lines), null, true);
  }
}
