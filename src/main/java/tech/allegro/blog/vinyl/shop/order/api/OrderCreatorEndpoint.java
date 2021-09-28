package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.CreateOrderJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.CreateOrderWithIdJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.OrderCreatedResponseJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {
  private final OrderCreatorHandler orderCreatorHandler;

  @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> create(@RequestBody CreateOrderJson items) {
    final var orderId = orderCreatorHandler.handle(items.toCommand());
    return buildResponse(orderId);
  }

  @PutMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> upsert(@PathVariable("orderId") String orderId,
                                                  @RequestBody CreateOrderWithIdJson items) {
    orderCreatorHandler.handle(items.toCommand());
    return buildResponse(new OrderId(orderId));
  }

  private ResponseEntity<OrderCreatedResponseJson> buildResponse(OrderId orderId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new OrderCreatedResponseJson(orderId.value()));
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
