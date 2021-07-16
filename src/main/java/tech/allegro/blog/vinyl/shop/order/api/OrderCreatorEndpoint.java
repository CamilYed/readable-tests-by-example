package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.AddItemsToOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.Item;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {

  private final OrderCreatorHandler orderCreatorHandler;

  @PostMapping("/orders/items")
  ResponseEntity<Void> items(@PathVariable String orderId, @RequestBody ModificationJson items) {
    orderCreatorHandler.handle(items.toCommand(orderId));
    return ResponseEntity.accepted().body(null);

  }

  record ModificationJson(
    List<ItemJson> items
  ) {

    AddItemsToOrderCommand toCommand(String orderId) {
      final var itemsToAdd = items.stream()
        .map(it -> new Item(new VinylId(it.productId), new Money(Double.parseDouble(it.price))))
        .collect(Collectors.toList());
      return new AddItemsToOrderCommand(new OrderId(orderId), itemsToAdd);
    }
  }

  record ItemJson(String productId, String price) {
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
