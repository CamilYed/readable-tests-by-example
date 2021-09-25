package tech.allegro.blog.vinyl.shop.order.api;

import lombok.Data;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@RestController
class OrderPaymentsEndpoint {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderPaymentsEndpoint.class);
  private final OrderPaymentHandler paymentHandler;

  public OrderPaymentsEndpoint(OrderPaymentHandler paymentHandler) {
    this.paymentHandler = paymentHandler;
  }

  @PutMapping(value = "/orders/{orderId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> payments(@PathVariable String orderId, @RequestBody PayOrderJson payOrderJson) {
    final var command = payOrderJson.toCommand(orderId);
    paymentHandler.handle(command);
    return ResponseEntity.accepted().build();
  }

  @Data
  static class PayOrderJson {
    String clientId;
    MoneyJson cost;

    public PayOrderJson() {
    }

    PayOrderCommand toCommand(String orderId) {
      return PayOrderCommand.of(
        ClientId.of(clientId),
        OrderId.of(orderId),
        Money.of(cost.getAmount(), cost.getCurrency())
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
