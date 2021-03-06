package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    void add(OrderLine orderLine) {
      removeLineOf(orderLine.vinyl().vinylId());
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

    Optional<OrderLine> findLineToUpdate(VinylId vinylId) {
      return lines.stream()
        .filter(it -> it.vinyl.vinylId().equals(vinylId))
        .findFirst();
    }
  }

  record OrderLine(
    Vinyl vinyl,
    Quantity quantity) {

    Money lineCost() {
      return vinyl.unitPrice().multiply(quantity);
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
