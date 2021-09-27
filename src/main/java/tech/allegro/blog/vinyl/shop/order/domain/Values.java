package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;

import java.util.ArrayList;
import java.util.List;

public class Values {

  public record OrderId(String value) {
  }

  record OrderLines(List<OrderLine> lines) {
    static OrderLines empty() {
      return new OrderLines(new ArrayList<>());
    }

    void add(VinylId productId, Money price) {
      lines.add(OrderLine.create(productId, price));
    }

    Money total() {
      return lines
        .stream()
        .map(OrderLine::price)
        .reduce(Money.ZERO, Money::add);
    }
  }

  record OrderLine(VinylId productId,
                   Money price) {
    static OrderLine create(VinylId productId, Money price) {
      return new OrderLine(productId, price);
    }
  }

  public static record OrderDataSnapshot(ClientId clientId,
                                         OrderId orderId,
                                         Money cost,
                                         Money deliveryCost,
                                         List<Item> items,
                                         boolean unpaid) {

    public static record Item(VinylId vinylId,
                              Money cost) {
      public static Item of(VinylId vinylId, Money cost) {
        return new Item(vinylId, cost);
      }
    }
  }
}
