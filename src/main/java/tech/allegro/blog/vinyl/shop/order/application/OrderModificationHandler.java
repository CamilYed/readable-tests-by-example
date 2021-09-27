package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderModificationHandler {
  private final OrderRepository orderRepository;
  private final OrderFactory orderFactory;

  public void handle(AddItemsToOrderCommand command) {
    final var snapshot = orderRepository.findBy(command.orderId);
    snapshot.ifPresent(orderDataSnapshot -> {
        final var order = orderFactory.fromSnapshot(orderDataSnapshot);
        command.items.forEach(item -> tryAddItemToOrder(order, item));
        orderRepository.save(order.toSnapshot());
      }
    );
  }

  private void tryAddItemToOrder(Order order, Item item) {
    try {
      order.addItem(item.productId, item.price);
    } catch (Order.CanNotModifyPaidOrder e) {
      log.error("Can not modify paid order", e);
      throw e;
    }
  }

  public record AddItemsToOrderCommand(OrderId orderId,
                                       List<Item> items) {
  }

  public record Item(VinylId productId,
                     Money price) {
  }
}
