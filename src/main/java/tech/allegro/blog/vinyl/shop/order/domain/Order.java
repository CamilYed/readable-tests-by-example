package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChangeFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChangeFailedBecauseNotExists;
import tech.allegro.blog.vinyl.shop.order.domain.Events.ItemQuantityChanged;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderDomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseDifferentAmounts;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLine;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderLines;

import java.util.stream.Collectors;

import static lombok.AccessLevel.PACKAGE;
import static tech.allegro.blog.vinyl.shop.common.time.ClockProvider.systemClock;

@AllArgsConstructor(access = PACKAGE)
public class Order {
  @Getter
  private OrderId orderId;
  private ClientId clientId;
  private OrderLines orderLines;
  private Delivery delivery;
  private boolean unpaid;

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
    } else return orderPayFailedBecauseAlreadyPaid();
  }

  public OrderDomainEvent changeItemQuantity(VinylId product, QuantityChange change) {
    if (unpaid) {
      final var orderLine = orderLines.findLineToUpdate(product);
      if (orderLine.isPresent()) {
        final var lineToUpdate = orderLine.get();
        final var updatedLine = lineToUpdate.changeQuantity(change);
        orderLines.add(updatedLine);
        return itemQuantityChanged(product, updatedLine.quantity());
      }
      return itemDoesNotExists(product);
    }
    return itemQuantityChangeFailedBecauseAlreadyPaid(product);
  }

  public OrderDataSnapshot toSnapshot() {
    final var toMapOfOfVinylAndQuantity = Collectors.toMap(OrderLine::vinyl, OrderLine::quantity);
    return new OrderDataSnapshot(
      clientId,
      orderId,
      orderLines.totalCost(),
      delivery != null ? delivery.cost() : null,
      orderLines.lines()
        .stream()
        .collect(toMapOfOfVinylAndQuantity),
      unpaid
    );
  }

  private OrderPaid orderPaidSuccessfully() {
    return new OrderPaid(clientId, orderId, systemClock().instant(), orderValue(), delivery);
  }

  private Money orderValue() {
    return orderLines.totalCost();
  }

  private OrderDomainEvent amountToBePaidIsDifferent(Money difference) {
    return new OrderPayFailedBecauseDifferentAmounts(orderId, systemClock().instant(), difference);
  }

  private OrderDomainEvent orderPayFailedBecauseAlreadyPaid() {
    return new OrderPayFailedBecauseAlreadyPaid(orderId, systemClock().instant());
  }

  private OrderDomainEvent itemQuantityChangeFailedBecauseAlreadyPaid(VinylId product) {
    return new ItemQuantityChangeFailedBecauseAlreadyPaid(orderId, product);
  }

  private OrderDomainEvent itemDoesNotExists(VinylId vinylId) {
    return new ItemQuantityChangeFailedBecauseNotExists(orderId, vinylId);
  }

  private OrderDomainEvent itemQuantityChanged(VinylId vinylId, Quantity quantity) {
    return new ItemQuantityChanged(orderId, vinylId, quantity);
  }
}
