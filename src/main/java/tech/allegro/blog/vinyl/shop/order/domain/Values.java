package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class Values {

  public record OrderId(String value) {
  }

  record OrderLines(List<OrderLine> lines) {
    static OrderLines empty() {
      return new OrderLines(new ArrayList<>());
    }

    void add(Vinyl product, Quantity quantity) {
      final var line = findLineToUpdate(product.vinylId());
      lines.removeIf(orderLine -> orderLine.vinyl.vinylId().equals(product.vinylId()));
      line.ifPresentOrElse(it -> lines.add(it.orderLine.increment(quantity)),
        () -> lines.add(OrderLine.create(product, quantity)));
    }

    private Optional<OrderLineAndIndex> findLineToUpdate(VinylId vinylId) {
      final var index = IntStream.range(0, lines.size())
        .filter(i -> lines.get(i).vinyl().vinylId().equals(vinylId))
        .findFirst()
        .orElse(0);
      return lines.stream()
        .filter(it -> it.vinyl.vinylId().equals(vinylId))
        .findFirst()
        .map(it -> new OrderLineAndIndex(it, index));
    }

    private record OrderLineAndIndex(OrderLine orderLine,
                                     int index) {
    }

    Money totalCost() {
      return lines
        .stream()
        .map(OrderLine::lineCost)
        .reduce(Money.ZERO, Money::add);
    }
  }

  record OrderLine(Vinyl vinyl,
                   Quantity quantity
  ) {
    public Money lineCost() {
      return vinyl.price().multiply(quantity);
    }

    OrderLine increment(Quantity toAdd) {
      return new OrderLine(this.vinyl, quantity.add(toAdd));
    }

    static OrderLine create(Vinyl product, Quantity quantity) {
      return new OrderLine(product, quantity);
    }
  }

  public static record OrderDataSnapshot(ClientId clientId,
                                         OrderId orderId,
                                         Money cost,
                                         Money deliveryCost,
                                         Map<Vinyl, Quantity> items,
                                         boolean unpaid) {
  }
}
