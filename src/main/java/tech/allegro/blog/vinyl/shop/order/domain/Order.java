package tech.allegro.blog.vinyl.shop.order.domain;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;

import java.util.Optional;

@AllArgsConstructor
public class Order {
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private Delivery delivery;
  private boolean unpaid;

  public Either<AmountToPayIsDifferent, Optional<OrderPaidEvent>> pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.total().add(delivery.cost());
      if (amount.notEqualTo(toPay)) {
        unpaid = true;
        return amountToPayIsDifferent();
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

  private Either<AmountToPayIsDifferent, Optional<OrderPaidEvent>> amountToPayIsDifferent() {
    return Try.failure(new AmountToPayIsDifferent())
      .toEither()
      .map(it -> Optional.<OrderPaidEvent>empty())
      .mapLeft(it -> (AmountToPayIsDifferent) it);
  }

  private Either<AmountToPayIsDifferent, Optional<OrderPaidEvent>> orderPaidSuccessfully() {
    return Try.success(Optional.of(OrderPaidEvent.of(orderId)))
      .toEither(AmountToPayIsDifferent::new);
  }

  private Either<AmountToPayIsDifferent, Optional<OrderPaidEvent>> nothing() {
    return Try.success(Optional.<OrderPaidEvent>empty())
      .toEither(AmountToPayIsDifferent::new);
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

final class AmountToPayIsDifferent extends RuntimeException {
}
