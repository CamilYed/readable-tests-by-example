package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.json.ErrorsJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.PayOrderJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.IncorrectAmount;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.OrderAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.OrderNotFound;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequiredArgsConstructor
@ExtensionMethod({JsonsExtensions.class}) //TODO consider remove or replace with https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext
class OrderPaymentsEndpoint {
  private final OrderPaymentHandler paymentHandler;

  @PutMapping(value = "/orders/{orderId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> payments(@NotBlank @PathVariable String orderId,
                             @Valid @RequestBody PayOrderJson payOrderJson) {
    final var command = payOrderJson.toCommand(orderId);
    final var result = paymentHandler.handle(command);
    if (result.isError()) {
      return switch (result.error().cause()) {
        case OrderNotFound e -> toResponseEntity(e);
        case OrderAlreadyPaid __ -> toResponseEntity();
        case IncorrectAmount e -> toResponseEntity(e);
        default -> ResponseEntity.internalServerError().build();
      };
    }
    return ResponseEntity.accepted().build();
  }

  public static ResponseEntity<?> toResponseEntity(OrderNotFound e) {
    final var error = ErrorsJson.Error.builder()
      .withCode(HttpStatus.NOT_FOUND.toString())
      .withMessage("""
        Order with id: ${e.getOrderId()} not found!"""
        .replace("${e.getOrderId()}", e.getOrderId().value()))
      .withPath("$.order.orderId")
      .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  public static ResponseEntity<?> toResponseEntity() {
    return ResponseEntity.accepted().build();
  }

  public static ResponseEntity<?> toResponseEntity(IncorrectAmount e) {
    final var error = ErrorsJson.Error.builder()
      .withCode(HttpStatus.UNPROCESSABLE_ENTITY.toString())
      .withMessage("""
        Incorrect amount, difference is: ${e.getDifference()} !"""
        .replace("${e.getDifference()}", e.getDifference().value().toString()))
      .withPath("$.order.cost")
      .build();
    return ResponseEntity.unprocessableEntity().body(error);
  }
}
