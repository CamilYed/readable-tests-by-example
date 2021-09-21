package tech.allegro.blog.vinyl.shop.order.domain;

import lombok.Value;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;

import java.util.ArrayList;
import java.util.List;

public class Values {

  @Value(staticConstructor = "of")
  public static class OrderId {
    String value;
  }

  @Value(staticConstructor = "of")
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

  @Value(staticConstructor = "of")
  static class OrderLine {
    VinylId productId;
    Money price;

    static OrderLine create(VinylId productId, Money price) {
      return new OrderLine(productId, price);
    }
  }

  @Value(staticConstructor = "of")
  public static class OrderDataSnapshot {
    ClientId clientId;
    OrderId orderId;
    Money cost;
    Money deliveryCost;
    List<Item> items;
    boolean unpaid;

    @Value(staticConstructor = "of")
    static class Item {
      VinylId vinylId;
      Money cost;
    }
  }
}
