package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.client.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

@RestController
@RequiredArgsConstructor
class OrderPaymentsEndpoint {

  private final OrderPaymentHandler paymentHandler;

  @PostMapping("/orders/{orderId}/payments")
  ResponseEntity<Void> payments(@PathVariable String orderId, @RequestBody PaymentJson paymentJson) {
    final var command = paymentJson.toCommand(orderId);
    paymentHandler.handle(command);
    return ResponseEntity.accepted().body(null);
  }

  record PaymentJson(
    String clientId,
    String amount
  ) {
    PayOrderCommand toCommand(String orderId) {
      return new PayOrderCommand(
        new ClientId(clientId),
        new OrderId(orderId),
        new Money(Double.parseDouble(amount))
      );
    }
  }

  @ControllerAdvice
  private static class ExceptionHandlingAdvice {
    @ExceptionHandler(Throwable.class)
    ResponseEntity<FailureJson> handleUnexpetedError(Throwable e) {
      FailureJson errorMessage = new FailureJson(e.getMessage());
      return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
