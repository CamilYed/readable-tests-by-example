package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLine;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLines;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class OrderFactory {

  public Order fromSnapshot(OrderDataSnapshot snapshot) {
    final List<OrderLine> lines = snapshot.items().entrySet()
      .stream().map(it -> new OrderLine(it.getKey(), it.getValue()))
      .collect(toList());
    return new Order(
      snapshot.orderId(),
      snapshot.clientId(),
      new OrderLines(lines),
      new Delivery(snapshot.cost()),
      snapshot.unpaid()
    );
  }

  public Order createUnpaidOrder(OrderId orderId, ClientId clientId, Map<Vinyl, Quantity> items) {
    return create(orderId, clientId, items, true);
  }

  public Order create(OrderId orderId, ClientId clientId, Map<Vinyl, Quantity> items, boolean unpaid) {
    var lines = items.entrySet().stream()
      .map(entry -> OrderLine.create(entry.getKey(), entry.getValue()))
      .collect(toList());
    return new Order(orderId, clientId, new OrderLines(lines), null, unpaid);
  }
}
