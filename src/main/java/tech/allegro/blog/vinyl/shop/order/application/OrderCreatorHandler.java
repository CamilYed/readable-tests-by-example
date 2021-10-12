package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.result.Result;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.Map;

@RequiredArgsConstructor
public class OrderCreatorHandler {
  private final OrderFactory orderFactory;
  private final OrderRepository orderRepository;

  public Result<OrderDataSnapshot> handle(CreateOrderCommand command) {
    return Result.of(() -> {
      final var orderId = orderRepository.nextId();
      final var order = orderFactory.createUnpaidOrder(orderId, command.clientId, command.items);
      orderRepository.save(order.toSnapshot());
      return order.toSnapshot();
    });
  }

  public Result<OrderDataSnapshot> handle(CreateOrderWithIdCommand command) {
    return Result.of(() -> {
      final var order = orderFactory.createUnpaidOrder(command.orderId, command.clientId, command.items);
      orderRepository.save(order.toSnapshot());
      return order.toSnapshot();
    });
  }

  public record CreateOrderCommand(
    ClientId clientId,
    Map<Vinyl, Quantity> items) {
  }

  public record CreateOrderWithIdCommand(
    OrderId orderId,
    ClientId clientId,
    Map<Vinyl, Quantity> items) {
  }
}
