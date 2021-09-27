package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

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
    orderRepository.save(order.toSnapshot());
    return order.getOrderId();
  }

  public OrderId handle(CreateOrderWithIdCommand command) {
    final var order = orderFactory.createUnpaidOrder(command.orderId, command.clientId, command.getItemsAsMap());
    orderRepository.save(order.toSnapshot());
    return order.getOrderId();
  }

  public record CreateOrderCommand(ClientId clientId,
                                   List<Item> items) {
    Map<VinylId, Money> getItemsAsMap() {
      return items.stream()
        .collect(Collectors.toMap(Item::productId, Item::price));
    }
  }

  public record CreateOrderWithIdCommand(OrderId orderId,
                                         ClientId clientId,
                                         List<Item> items) {
    Map<VinylId, Money> getItemsAsMap() {
      return items.stream()
        .collect(Collectors.toMap(Item::productId, Item::price));
    }
  }

  public record Item(VinylId productId,
                     Money price) {
  }
}
