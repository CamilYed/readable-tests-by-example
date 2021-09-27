package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.events.Event;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPaid;
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPayFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPayFailedBecauseAmountIsDifferent;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot.Item;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLines;

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

  public Event pay(Money amount, Delivery delivery) {
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
    if (orderLines == null) //TODO remove
      orderLines = OrderLines.empty();
    if (unpaid) {
      orderLines.add(productId, price);
    } else throw new CanNotModifyPaidOrder(); // else return orderAlreadyPaid();
  }

  public static final class CanNotModifyPaidOrder extends RuntimeException {
  } // TODO replace with event

  public Money orderValue() {
    return orderLines.total();
  }

  public OrderDataSnapshot toSnapshot() {
    return OrderDataSnapshot.of(
      clientId,
      orderId,
      orderLines.total(),
      delivery.getCost(),
      orderLines.getLines().stream().map(it -> Item.of(it.getProductId(), it.getPrice())).collect(toList()),
      unpaid
    );
  }

  private OrderPaid orderPaidSuccessfully() {
    return new OrderPaid(clientId, orderId, ClockProvider.systemClock().instant(), orderValue(), delivery);
  }

  private OrderPayFailedBecauseAmountIsDifferent amountToBePaidIsDifferent() {
    return new OrderPayFailedBecauseAmountIsDifferent(orderId, ClockProvider.systemClock().instant());
  }

  private OrderPayFailedBecauseAlreadyPaid orderAlreadyPaid() {
    return new OrderPayFailedBecauseAlreadyPaid(orderId, ClockProvider.systemClock().instant());
  }
}
