package tech.allegro.blog.vinyl.shop.order.application.search;

import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;

import java.util.List;

public record PaidClientOrdersView(
  List<OrderDataSnapshot> orders) {
}
