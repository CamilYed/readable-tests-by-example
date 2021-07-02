package tech.allegro.blog.vinyl.shop.order.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;

@AllArgsConstructor
public class Order {
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private boolean unpaid;

  void pay(Money amount) {
    if (unpaid) {
      final var orderValue = orderLines.total();
      if (orderValue.notEqualTo(amount)) {
        unpaid = true;
      }
    }
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
