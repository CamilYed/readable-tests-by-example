package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderDomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseDifferentAmounts;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLine;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLines;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PACKAGE;

@AllArgsConstructor(access = PACKAGE)
public class Order {
  @Getter
  private OrderId orderId;
  private ClientId clientId;
  private OrderLines orderLines;
  private Delivery delivery;
  private boolean unpaid; // TODO replace with enum with values DRAFT, PAID etc.

  public OrderDomainEvent pay(Money amount, Delivery delivery) {
    if (unpaid) {
      final var toPay = orderLines.totalCost().add(delivery.cost());
      if (amount.equalTo(toPay)) {
        this.delivery = delivery;
        unpaid = false;
        return orderPaidSuccessfully();
      } else {
        return amountToBePaidIsDifferent(amount.subtract(toPay));
      }
    } else return orderAlreadyPaid();
  }

  public void addItem(Vinyl product, Quantity quantity) {
    if (orderLines == null)
      orderLines = OrderLines.empty();
    if (unpaid) {
      orderLines.add(product, quantity);
    } else throw new CanNotModifyPaidOrder();
  }

  public static final class CanNotModifyPaidOrder extends RuntimeException {
  } // TODO replace with event

  public Money orderValue() {
    return orderLines.totalCost();
  }

  public OrderDataSnapshot toSnapshot() {
    return new OrderDataSnapshot(
      clientId,
      orderId,
      orderLines.totalCost(),
      delivery != null ? delivery.cost() : null,
      orderLines.lines().stream().collect(Collectors.toMap(OrderLine::vinyl, OrderLine::quantity)),
      unpaid
    );
  }

  private OrderPaid orderPaidSuccessfully() {
    return new OrderPaid(clientId, orderId, ClockProvider.systemClock().instant(), orderValue(), delivery);
  }

  private OrderPayFailedBecauseDifferentAmounts amountToBePaidIsDifferent(Money difference) {
    return new OrderPayFailedBecauseDifferentAmounts(orderId, ClockProvider.systemClock().instant(), difference);
  }

  private OrderPayFailedBecauseAlreadyPaid orderAlreadyPaid() {
    return new OrderPayFailedBecauseAlreadyPaid(orderId, ClockProvider.systemClock().instant());
  }
}
