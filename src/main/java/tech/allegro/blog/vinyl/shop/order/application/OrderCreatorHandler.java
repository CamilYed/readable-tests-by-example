package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderCreatorHandler {
  private final OrderFactory orderFactory;
  private final OrderRepository orderRepository;

  public OrderId handle(CreateOrderCommand command) {
    final var orderId = orderRepository.nextId();
    final var order = orderFactory.createUnpaidOrder(orderId, command.clientId, command.getItemsAsMap());
    orderRepository.save(order);
    return order.getOrderId();
  }

  public OrderId handle(CreateOrderWithIdCommand command) {
    final var order = orderFactory.createUnpaidOrder(command.orderId, command.clientId, command.getItemsAsMap());
    orderRepository.save(order);
    return order.getOrderId();
  }

  @Value(staticConstructor = "of")
  static public class CreateOrderCommand {
    ClientId clientId;
    List<Item> items;

    Map<VinylId, Money> getItemsAsMap() {
      return items.stream()
        .collect(Collectors.toMap(Item::getProductId, Item::getPrice));
    }
  }

  @Value(staticConstructor = "of")
  static public class CreateOrderWithIdCommand {
    OrderId orderId;
    ClientId clientId;
    List<Item> items;

    Map<VinylId, Money> getItemsAsMap() {
      return items.stream()
        .collect(Collectors.toMap(Item::getProductId, Item::getPrice));
    }
  }

  @Value(staticConstructor = "of")
  static public class Item {
    VinylId productId;
    Money price;
  }
}
