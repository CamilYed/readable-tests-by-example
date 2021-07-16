package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPaidEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class Order {
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private Delivery delivery;
  private boolean unpaid;

  public OrderPaidEvent pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.total().add(delivery.getCost());
      if (amount.equalTo(toPay)) {
        this.delivery = delivery;
        unpaid = true;
        return orderPaidSuccessfully();
      } else {
        unpaid = true;
        throw amountToBePaidIsDifferent();
      }
    }
    throw orderAlreadyPaid();
  }

  public void addItem(VinylId productId, Money price) {
    if (unpaid) {
      orderLines.add(productId, price);
    } else throw new CanNotModifyPaidOrder();
  }

  public Money orderValue() {
    return orderLines.total();
  }

  private OrderPaidEvent orderPaidSuccessfully() {
    final var totalCost = this.orderValue().add(delivery.getCost());
    return new OrderPaidEvent(orderId, Instant.now(), totalCost, delivery);
  }


  private AmountToPayIsDifferent amountToBePaidIsDifferent() {
    return new AmountToPayIsDifferent();
  }

  public static final class AmountToPayIsDifferent extends RuntimeException {
  }

  private OrderAlreadyPaid orderAlreadyPaid() {
    return new OrderAlreadyPaid();
  }

  public static final class OrderAlreadyPaid extends RuntimeException {
  }

  public static final class CanNotModifyPaidOrder extends RuntimeException {
  }
}

record OrderLines(
  List<OrderLine> lines
) {

  void add(VinylId productId, Money price) {
    lines.add(OrderLine.create(productId, price));
  }

  private OrderLines() {
    this(Collections.emptyList());
  }

  static OrderLines empty() {
    return new OrderLines();
  }

  Money total() {
    return lines
      .stream()
      .map(OrderLine::price)
      .reduce(Money.ZERO, Money::add);
  }
}

record OrderLine(VinylId productId, Money price) {
  static OrderLine create(VinylId productId, Money price) {
    return new OrderLine(productId, price);
  }
}
