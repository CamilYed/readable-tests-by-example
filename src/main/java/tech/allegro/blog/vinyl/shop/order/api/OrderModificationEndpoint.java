package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.result.Result;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ChangeOrderItemQuantityJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.CanNotChangeQuantityOfNotExistingItem;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.CanNotModifyPaidOrder;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.OrderNotFound;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Slf4j
@RestController
@RequiredArgsConstructor
@ExtensionMethod({JsonsExtensions.class})
class OrderModificationEndpoint {
  private final OrderModificationHandler orderModificationHandler;

  @PatchMapping(value = "/orders/{orderId}/items/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<?> changeItemQuantity(@NotBlank @PathVariable String orderId,
                                   @NotBlank @PathVariable String productId,
                                   @Valid @RequestBody ChangeOrderItemQuantityJson changeQuantity) {
    final var result = Result.of(() -> orderModificationHandler.handle(changeQuantity.toCommand(productId, orderId)));
    if (result.isError()) {
      return switch (result.error().cause()) {
        case OrderNotFound e -> toResponseEntity(e);
        case CanNotModifyPaidOrder e -> toResponseEntity(e);
        case CanNotChangeQuantityOfNotExistingItem e -> toResponseEntity(e);
        case ConstraintViolationException e -> toResponseEntity(e);
        default -> ResponseEntity.internalServerError().build();
      };
    }
    return ResponseEntity.accepted().build();
  }

  private ResponseEntity<?> toResponseEntity(OrderNotFound e) {
    log.error("Order with id: {} does not exists!", e.getOrderId(), e);
    final var message = new FailureJson("""
      Order with id: $id can not be modified because is paid!"""
      .replace("$id", e.getOrderId().value())
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
  }

  private ResponseEntity<?> toResponseEntity(CanNotModifyPaidOrder e) {
    log.error("Order with id: {} can not be modified because is paid!", e.getOrderId(), e);
    final var message = new FailureJson("""
      Order with id: $id can not be modified because is paid!"""
      .replace("$id", e.getOrderId().value())
    );
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(message);
  }

  private ResponseEntity<?> toResponseEntity(CanNotChangeQuantityOfNotExistingItem e) {
    log.error("Order with id: {} can not be modified because does not contains item: {}!", e.getOrderId(), e.getVinylId(), e);
    final var message = new FailureJson("""
      Order with id: $id can not be modified because does not contains item: $itemId"""
      .replace("$id", e.getOrderId().value())
      .replace("$itemId", e.getVinylId().value())
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
  }

  private ResponseEntity<?> toResponseEntity(ConstraintViolationException e) {
    log.error("Illegal argument exception", e);
    return ResponseEntity.badRequest().build();
  }
}
