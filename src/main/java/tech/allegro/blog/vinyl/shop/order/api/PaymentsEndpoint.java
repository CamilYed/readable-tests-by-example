package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler.Failure;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.PaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.PaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

@RestController
@RequiredArgsConstructor
class PaymentsEndpoint {

  private final PaymentHandler paymentHandler;

  @PostMapping("/orders/{orderId}/payments")
  ResponseEntity<Void> payments(@PathVariable String orderId, @RequestBody PaymentJson paymentJson) {
    final var command = paymentJson.toCommand(orderId);
    final var result = paymentHandler.handle(command);
    return result
      .mapLeft(Failure::toJson)
      .mapLeft(it -> ResponseEntity.unprocessableEntity().body(it))
      .map(it -> ResponseEntity.accepted().body(it))
      .get();
  }

  record PaymentJson(
    String transactionId,
    String amount
  ) {
    PayOrderCommand toCommand(String orderId) {
      return new PayOrderCommand(
        new OrderId(orderId),
        new Money(Double.parseDouble(amount))
      );
    }
  }
}
