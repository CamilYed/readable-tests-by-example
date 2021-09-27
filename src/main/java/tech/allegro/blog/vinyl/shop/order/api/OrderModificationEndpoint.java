package tech.allegro.blog.vinyl.shop.order.api;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.List;
import java.util.stream.Collectors;

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.AddItemsToOrderCommand;
import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.Item;

@RestController
class OrderModificationEndpoint {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderModificationEndpoint.class);
  private final OrderModificationHandler orderModificationHandler;

  public OrderModificationEndpoint(OrderModificationHandler orderModificationHandler) {
    this.orderModificationHandler = orderModificationHandler;
  }

  @PutMapping(value = "/orders/{orderId}/items", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> items(@PathVariable String orderId, @RequestBody OrderItemsJson items) {
    orderModificationHandler.handle(items.toCommand(orderId));
    return ResponseEntity.accepted().build();
  }

  record OrderItemsJson(List<ItemJson> items) {
    AddItemsToOrderCommand toCommand(String orderId) {
      final var itemsToAdd = items.stream()
        .map(it -> new Item(new VinylId(it.productId), Money.of(it.cost.amount(), it.cost.currency())))
        .collect(Collectors.toList());
      return new AddItemsToOrderCommand(new OrderId(orderId), itemsToAdd);
    }
  }

  static record ItemJson(String productId,
                         MoneyJson cost) {

  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
