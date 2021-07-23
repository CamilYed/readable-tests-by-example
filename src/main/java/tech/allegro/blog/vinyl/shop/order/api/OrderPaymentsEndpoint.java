package tech.allegro.blog.vinyl.shop.order.api;

import lombok.Data;
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

  @Data
  static class PaymentJson {
    private final String clientId;
    private final String amount;

    PayOrderCommand toCommand(String orderId) {
      return PayOrderCommand.of(
        ClientId.of(clientId),
        OrderId.of(orderId),
        Money.of(amount)
      );
    }
  }

  @ControllerAdvice
  private static class ExceptionHandlingAdvice {
    @ExceptionHandler(Throwable.class)
    ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
      FailureJson errorMessage = new FailureJson(e.getMessage());
      return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
