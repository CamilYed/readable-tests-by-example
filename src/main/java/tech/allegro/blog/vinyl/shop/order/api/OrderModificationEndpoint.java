package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.OrderItemsJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderModificationEndpoint {
  private final OrderModificationHandler orderModificationHandler;

  @PutMapping(value = "/orders/{orderId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> items(@PathVariable String orderId, @RequestBody OrderItemsJson items) {
    orderModificationHandler.handle(items.toCommand(orderId));
    return ResponseEntity.accepted().build();
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
