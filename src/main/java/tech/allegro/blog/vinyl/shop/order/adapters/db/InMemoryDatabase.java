package tech.allegro.blog.vinyl.shop.order.adapters.db;

import lombok.experimental.ExtensionMethod;
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson;
import tech.allegro.blog.vinyl.shop.order.application.search.ClientOrdersView.OrderDataJson.UnitPrice;
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@ExtensionMethod({InMemoryDatabase.Extensions.class})
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
          it.toOrderCostJson(),
          it.deliveryCost().toDeliveryCostJson(),
          it.items().toOrderDataItemsJson(),
          it.unpaid()
        )
      ).collect(toList());
  }

  static class Extensions {

    public static OrderDataJson.DeliveryCost toDeliveryCostJson(Money it) {
      return it != null ? new OrderDataJson.DeliveryCost(it.toString(), it.currency().toString()) : null;
    }

    public static OrderDataJson.OrderCost toOrderCostJson(OrderDataSnapshot it) {
      return new OrderDataJson.OrderCost(it.cost().toString(), it.cost().currency().toString());
    }

    public static List<OrderDataJson.Item> toOrderDataItemsJson(Map<Vinyl, Quantity> items) {
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
}
