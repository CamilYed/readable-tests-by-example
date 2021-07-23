package tech.allegro.blog.vinyl.shop.order.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.catalogue.VinylId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.Item;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {

  private final OrderModificationHandler orderCreatorHandler;

  @PutMapping("/orders/{orderId}/items")
  ResponseEntity<Void> items(@PathVariable String orderId, @RequestBody ModificationJson items) {
    orderCreatorHandler.handle(items.toCommand(orderId));
    return ResponseEntity.accepted().body(null);

  }

  @Data
  static class ModificationJson {
    private final  List<ItemJson> items;

    AddItemsToOrderCommand toCommand(String orderId) {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(Double.parseDouble(it.price))))
        .collect(Collectors.toList());
      return AddItemsToOrderCommand.of(OrderId.of(orderId), itemsToAdd);
    }
  }

  @Data
  static class ItemJson {
    private final String productId;
    private final String price;
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
