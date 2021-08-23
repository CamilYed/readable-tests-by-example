package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.Order.CanNotModifyPaidOrder;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OrderModificationHandler implements CommandHandler<OrderModificationHandler.AddItemsToOrderCommand> {

  private final OrderRepository orderRepository;

  @Override
  public void handle(AddItemsToOrderCommand command) {
    final var clientOrder = orderRepository.findBy(command.orderId);
    clientOrder.ifPresent(order -> {
        command.items.forEach(item -> tryAddItemToOrder(order, item));
        orderRepository.save(order);
      }
    );
  }

  private void tryAddItemToOrder(Order order, Item item) {
    try {
      order.addItem(item.productId, item.price);
    } catch (CanNotModifyPaidOrder e) {
      log.error("Can not modify paid order", e);
      throw e;
    }
  }

  @Value(staticConstructor = "of")
  static public class AddItemsToOrderCommand {
    OrderId orderId;
    List<Item> items;
  }

  @Value(staticConstructor = "of")
  static public class Item {
    VinylId productId;
    Money price;
  }
}
