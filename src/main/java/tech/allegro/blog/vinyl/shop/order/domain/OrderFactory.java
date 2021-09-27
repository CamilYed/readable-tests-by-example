package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLine;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLines;

import java.util.Map;
import java.util.stream.Collectors;

public class OrderFactory {

  public Order fromSnapshot(OrderDataSnapshot snapshot) {
    return new Order(
      snapshot.orderId(),
      snapshot.clientId(),
      new OrderLines(
        snapshot.items().stream().map(it -> new OrderLine(it.vinylId(), it.cost())).collect(Collectors.toList())
      ),
      new Delivery(snapshot.cost()),
      snapshot.unpaid()
    );
  }

  public Order createUnpaidOrder(OrderId orderId, ClientId clientId, Map<VinylId, Money> items) {
    return create(orderId, clientId, items, true);
  }

  public Order create(OrderId orderId, ClientId clientId, Map<VinylId, Money> items, boolean unpaid) {
    var lines = items.entrySet().stream()
      .map(it -> OrderLine.create(it.getKey(), it.getValue()))
      .collect(Collectors.toList());
    return new Order(orderId, clientId, new OrderLines(lines), null, unpaid);
  }
}
