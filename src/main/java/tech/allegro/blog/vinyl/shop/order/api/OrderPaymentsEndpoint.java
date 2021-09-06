package tech.allegro.blog.vinyl.shop.order.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

@RestController
@Slf4j
@RequiredArgsConstructor
class OrderPaymentsEndpoint {
  private final OrderPaymentHandler paymentHandler;

  @PutMapping(value = "/orders/{orderId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> payments(@PathVariable String orderId, @RequestBody PaymentJson paymentJson) {
    final var command = paymentJson.toCommand(orderId);
    paymentHandler.handle(command);
    return ResponseEntity.accepted().build();
  }

  @Data
  static class PaymentJson {
    String clientId;
    String amount;

    PayOrderCommand toCommand(String orderId) {
      return PayOrderCommand.of(
        ClientId.of(clientId),
        OrderId.of(orderId),
        Money.of(amount)
      );
    }
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during payment", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
