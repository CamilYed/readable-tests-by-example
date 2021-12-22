package tech.allegro.blog.vinyl.shop.order.adapters.db;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.UnitPrice;
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

class InMemoryDatabase implements OrderRepository, FindClientOrders {

  private final Map<OrderId, OrderDataSnapshot> orders = new ConcurrentHashMap<>();

  @Override
  public OrderId nextId() {
    return new OrderId(UUID.randomUUID().toString());
  }

  @Override
  public Optional<OrderDataSnapshot> findBy(OrderId orderId) {
    return Optional.ofNullable(orders.get(orderId));
  }

  @Override
  public void save(OrderDataSnapshot order) {
    orders.put(order.orderId(), order);
  }

  @Override
  public ClientOrdersView findOne(OrderId orderId) {
    List<OrderDataSnapshot> orders = new ArrayList<>(1);
    findBy(orderId).ifPresent(orders::add);
    return new ClientOrdersView(toJson(orders));
  }

  private static List<OrderDataJson> toJson(List<OrderDataSnapshot> orders) {
    return orders.stream()
      .map(it -> new OrderDataJson(
          it.clientId().value(),
          it.orderId().value(),
          new OrderDataJson.OrderCost(it.cost().toString(), it.cost().currency().toString()),
          it.deliveryCost() != null ? new OrderDataJson.DeliveryCost(it.deliveryCost().toString(), it.deliveryCost().currency().toString()) : null,
          toJson(it.items()),
          it.unpaid()
        )
      ).collect(toList());
  }

  private static List<OrderDataJson.Item> toJson(Map<Vinyl, Quantity> items) {
    return items.entrySet().stream()
      .map(it ->
        new OrderDataJson.Item(
          it.getKey().vinylId().value(),
          new UnitPrice(it.getKey().unitPrice().value().toString(), it.getKey().unitPrice().currency().toString()),
          it.getValue().value())
      )
      .collect(toList());
  }
}
