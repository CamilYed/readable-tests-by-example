package tech.allegro.blog.vinyl.shop.order.application.search;

import java.util.List;

public record ClientOrdersView(List<OrderDataJson> orders) {

  public record OrderDataJson(
    String clientId,
    String orderId,
    OrderCost orderCost,
    DeliveryCost deliveryCost,
    List<Item> items,
    boolean unpaid
  ) {

    public record OrderCost(
      String cost,
      String currency
    ) {
    }

    public record DeliveryCost(
      String cost,
      String currency
    ) {
    }

    public record Item(
      String productId,
      UnitPrice unitPrice,
      int quantity
    ) {
    }

    public record UnitPrice(
      String price,
      String currency
    ) {
    }
  }
}
