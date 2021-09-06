package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPaid;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPayFailed;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Order {
  @Getter
  private final OrderId orderId;
  private final OrderLines orderLines = OrderLines.empty();
  private Delivery delivery;
  private boolean unpaid;

  public DomainEvent pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.total().add(delivery.getCost());
      if (amount.equalTo(toPay)) {
        this.delivery = delivery;
        unpaid = false;
        return orderPaidSuccessfully();
      } else {
        return amountToBePaidIsDifferent();
      }
    } else return orderAlreadyPaid();
  }

  public void addItem(VinylId productId, Money price) {
    if (unpaid) {
      orderLines.add(productId, price);
    } else throw new CanNotModifyPaidOrder();
  }

  public Money orderValue() {
    return orderLines.total();
  }

  private OrderPaid orderPaidSuccessfully() {
    return OrderPaid.of(orderId, ClockProvider.systemClock().instant(), orderValue(), delivery);
  }

  private OrderPayFailed amountToBePaidIsDifferent() {
    return OrderPayFailed.of(orderId, ClockProvider.systemClock().instant(), OrderPayFailed.Reason.AMOUNT_IS_DIFFERENT);
  }

  private OrderPayFailed orderAlreadyPaid() {
    return OrderPayFailed.of(orderId, ClockProvider.systemClock().instant(), OrderPayFailed.Reason.ALREADY_PAID);
  }

  public static final class CanNotModifyPaidOrder extends RuntimeException {
  }

  @Value
  static class OrderLines {
    List<OrderLine> lines;

    static OrderLines empty() {
      return new OrderLines(new ArrayList<>());
    }

    void add(VinylId productId, Money price) {
      lines.add(OrderLine.create(productId, price));
    }

    Money total() {
      return lines
        .stream()
        .map(OrderLine::getPrice)
        .reduce(Money.ZERO, Money::add);
    }
  }
}

@Value(staticConstructor = "of")
class OrderLine {
  VinylId productId;
  Money price;

  static OrderLine create(VinylId productId, Money price) {
    return new OrderLine(productId, price);
  }
}
