package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPaidEvent;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class Order {
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private Delivery delivery;
  private boolean unpaid;

  public Optional<OrderPaidEvent> pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.total().add(delivery.cost());
      if (amount.notEqualTo(toPay)) {
        unpaid = true;
        throw amountToBePaidIsDifferent();
      } else {
        this.delivery = delivery;
        return orderPaidSuccessfully();
      }
    }
    return nothing();
  }

  public Money orderValue() {
    return orderLines.total();
  }

  private AmountToPayIsDifferent amountToBePaidIsDifferent() {
    return new AmountToPayIsDifferent();
  }

  private Optional<OrderPaidEvent> orderPaidSuccessfully() {
    return Optional.of(new OrderPaidEvent(orderId, Instant.now()));
  }

  private Optional<OrderPaidEvent> nothing() {
    return Optional.empty();
  }

  public static final class AmountToPayIsDifferent extends RuntimeException {
  }
}

record OrderLines(
  List<OrderLine> lines
) {

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
}
