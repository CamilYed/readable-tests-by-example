package tech.allegro.blog.vinyl.shop.order.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.Item;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {
  private final OrderModificationHandler orderCreatorHandler;

  @PutMapping(value = "/orders/{orderId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> items(@PathVariable String orderId, @RequestBody OrderItemsJson items) {
    orderCreatorHandler.handle(items.toCommand(orderId));
    return ResponseEntity.accepted().build();
  }

  @Data
  static class OrderItemsJson {
    private final  List<ItemJson> items;

    AddItemsToOrderCommand toCommand(String orderId) {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(it.price)))
        .collect(Collectors.toList());
      return AddItemsToOrderCommand.of(OrderId.of(orderId), itemsToAdd);
    }
  }

  @Data
  static class ItemJson {
    private final String productId;
    private final String price;
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
