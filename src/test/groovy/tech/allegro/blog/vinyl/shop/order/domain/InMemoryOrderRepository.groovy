package tech.allegro.blog.vinyl.shop.order.domain

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.common.volume.Quantity
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders

import java.util.concurrent.ConcurrentHashMap

import static java.util.stream.Collectors.toList
import static tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.*
import static tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.*
import static tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.DeliveryCost
import static tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.Item
import static tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.OrderCost
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId

class InMemoryOrderRepository implements OrderRepository, FindClientOrders {

  private final Map<OrderId, OrderDataSnapshot> orders = new ConcurrentHashMap<OrderId, OrderDataSnapshot>()

  @Override
  Optional<OrderDataSnapshot> findBy(OrderId orderId) {
    return Optional.ofNullable(orders.get(orderId))
  }

  @Override
  void save(OrderDataSnapshot order) {
    orders.put(order.orderId(), order)
  }

  @Override
  ClientOrdersView findOne(OrderId orderId) {
    List<OrderDataSnapshot> orders = new ArrayList<>(1)
    findBy(orderId).ifPresent(orders::add)
    return new ClientOrdersView(toJson(orders))
  }

  private static List<OrderDataJson> toJson(List<OrderDataSnapshot> orders) {
    return orders.stream()
      .map(it ->
        new OrderDataJson(
          it.clientId().value(),
          it.orderId().value(),
          new OrderCost(it.cost().toString(), it.cost().currency().toString()),
          it.deliveryCost() != null ? new DeliveryCost(it.deliveryCost().toString(), it.deliveryCost().currency().toString()) : null,
          toJson(it.items()),
          it.unpaid()
        )
      ).collect(toList());
  }

  private static List<Item> toJson(Map<Vinyl, Quantity> items) {
    return items.entrySet().stream()
      .map(it ->
        new Item(
          it.getKey().vinylId().value(),
          new UnitPrice(it.getKey().unitPrice().value().toString(), it.getKey().unitPrice().currency().toString()),
          it.getValue().value())
      )
      .collect(toList())
  }
}
