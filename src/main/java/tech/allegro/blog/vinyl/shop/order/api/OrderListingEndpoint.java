package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.result.Result;
import tech.allegro.blog.vinyl.shop.order.application.search.FindClientOrders;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderListingEndpoint {
  private final FindClientOrders findClientOrders;

  @GetMapping("/orders-view/{orderId}")
  ResponseEntity<?> get(@NotBlank @PathVariable("orderId") String orderId) {
    final var result = Result.of(() -> findClientOrders.findOne(new OrderId(orderId)));
    final var clientOrdersView = result.getSuccessOrThrowError();
    if (clientOrdersView.orders().isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(clientOrdersView);
  }
}
