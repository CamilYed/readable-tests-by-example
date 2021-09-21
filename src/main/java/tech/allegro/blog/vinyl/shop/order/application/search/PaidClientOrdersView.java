package tech.allegro.blog.vinyl.shop.order.application.search;

import lombok.Data;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;

import java.util.List;

@Data(staticConstructor = "of")
public class PaidClientOrdersView {
  private final List<OrderDataSnapshot> orders;
}
