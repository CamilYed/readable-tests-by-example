package tech.allegro.blog.vinyl.shop.order.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;

@AllArgsConstructor
public class Order {
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private Delivery delivery;
  private boolean unpaid;

  public void pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.total().add(delivery.cost());
      if (toPay.notEqualTo(amount)) {
        unpaid = true;
      }
      this.delivery = delivery;
    }
  }

  public Money orderValue() {
    return orderLines.total();
  }
}

record OrderLines(
  List<OrderLine> lines
) {

  private OrderLines() {
    this(List.empty());
  }

  static OrderLines empty() {
    return new OrderLines();
  }

  Money total() {
    return lines
      .map(OrderLine::price)
      .foldLeft(Money.ZERO, Money::add);
  }
}

record OrderLine(VinylId productId, Money price) {
}
