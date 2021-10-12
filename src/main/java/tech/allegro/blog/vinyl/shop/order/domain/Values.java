package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange;

import java.util.*;

public class Values {

  public static record OrderId(String value) {
  }

  public static record OrderDataSnapshot(
    ClientId clientId,
    OrderId orderId,
    Money cost,
    Money deliveryCost,
    Map<Vinyl, Quantity> items,
    boolean unpaid) {
  }

  record OrderLines(List<OrderLine> lines) {

    OrderLines(List<OrderLine> lines) {
      this.lines = Objects.requireNonNullElseGet(lines, ArrayList::new);
    }

    Optional<OrderLine> changeQuantity(VinylId vinylId, QuantityChange change) {
      final var orderLine = findLineToUpdate(vinylId);
      return orderLine.map(line -> line.changeQuantity(change));
    }

    void add(OrderLine orderLine) {
      lines.add(orderLine);
    }

    void removeLineOf(VinylId productId) {
      lines.removeIf(line -> line.isOfProduct(productId));
    }

    Money totalCost() {
      return lines
        .stream()
        .map(OrderLine::lineCost)
        .reduce(Money.ZERO, Money::add);
    }

    private Optional<OrderLine> findLineToUpdate(VinylId vinylId) {
      return lines.stream()
        .filter(it -> it.vinyl.vinylId().equals(vinylId))
        .findFirst();
    }
  }

  record OrderLine(
    Vinyl vinyl,
    Quantity quantity) {

    Money lineCost() {
      return vinyl.price().multiply(quantity);
    }

    OrderLine changeQuantity(QuantityChange change) {
      return new OrderLine(this.vinyl, quantity.change(change));
    }

    boolean isOfProduct(VinylId vinylId) {
      return vinyl.vinylId().equals(vinylId);
    }

    static OrderLine create(Vinyl product, Quantity quantity) {
      return new OrderLine(product, quantity);
    }
  }
}
