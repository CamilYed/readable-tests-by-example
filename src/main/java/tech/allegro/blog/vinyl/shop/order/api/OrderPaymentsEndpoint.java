package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.PayOrderJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.IncorrectAmount;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.OrderAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.OrderNotFound;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderPaymentsEndpoint {
  private final OrderPaymentHandler paymentHandler;

  @PutMapping(value = "/orders/{orderId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> payments(@PathVariable String orderId, @RequestBody PayOrderJson payOrderJson) {
    final var command = payOrderJson.toCommand(orderId);
    final var result = paymentHandler.handle(command);
    if (result.isError()) {
      return switch (result.error().cause()) {
        case OrderNotFound e -> toResponseEntity(e);
        case OrderAlreadyPaid e -> toResponseEntity(e);
        case IncorrectAmount e -> toResponseEntity(e);
        default -> ResponseEntity.internalServerError().build();
      };
    }
    return ResponseEntity.accepted().build();
  }

  private ResponseEntity<?> toResponseEntity(OrderNotFound e) {
    final var message = new FailureJson("""
      Order with id: ${e.getOrderId()} not found!"""
      .replace("${e.getOrderId()}", e.getOrderId().value())
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
  }

  private ResponseEntity<?> toResponseEntity(OrderAlreadyPaid e) {
    return ResponseEntity.accepted().build();
  }

  private ResponseEntity<?> toResponseEntity(IncorrectAmount e) {
    final var message = new FailureJson("""
      Incorrect amount, difference is: ${e.getDifference()} !"""
      .replace("${e.getDifference()}", e.getDifference().value().toString())
    );
    return ResponseEntity.unprocessableEntity().body(message);
  }

}
