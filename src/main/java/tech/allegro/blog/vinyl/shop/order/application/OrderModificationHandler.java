package tech.allegro.blog.vinyl.shop.order.application;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.result.Result;
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChangeFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChangeFailedBecauseNotExists;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChanged;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderDomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@Slf4j
@RequiredArgsConstructor
public class OrderModificationHandler {
  private final OrderRepository orderRepository;
  private final OrderFactory orderFactory;

  public Result<Void> handle(ChangeItemQuantityCommand command) {
    return Result.of(() -> {
      final var order = findOrderOrThrowNotFound(command.orderId);
      final var event = order.changeQuantity(command.item, command.quantityChange);
      raiseErrorWhenNeeded(event);
      orderRepository.save(order.toSnapshot());
    });
  }

  public record ChangeItemQuantityCommand(
    OrderId orderId,
    VinylId item,
    QuantityChange quantityChange) {
  }

  private Order findOrderOrThrowNotFound(OrderId orderId) {
    final var snapshot = orderRepository.findBy(orderId).orElseThrow(() -> new OrderNotFound(orderId));
    return orderFactory.fromSnapshot(snapshot);
  }

  private void raiseErrorWhenNeeded(OrderDomainEvent event) {
    switch (event) {
      case ItemQuantityChangeFailedBecauseAlreadyPaid e -> throw new CanNotModifyPaidOrder(e.orderId());
      case ItemQuantityChangeFailedBecauseNotExists e -> throw new CanNotChangeQuantityOfNotExistingItem(e.orderId(), e.notExistingItem());
      case ItemQuantityChanged e -> log.info("Item quantity changed = {}", e);
      default -> log.warn("Unhandled use case event = {}", event);
    }
  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class OrderNotFound extends RuntimeException {
    OrderId orderId;
  }
  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class CanNotModifyPaidOrder extends RuntimeException {
    OrderId orderId;

  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class CanNotChangeQuantityOfNotExistingItem extends RuntimeException {
    OrderId orderId;
    VinylId vinylId;
  }
}
