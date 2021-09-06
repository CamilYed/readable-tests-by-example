package tech.allegro.blog.vinyl.shop.order.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderWithItemsCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.Item;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {
  private final OrderCreatorHandler orderCreatorHandler;

  @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedJson> create(@RequestBody OrderItemsJson items) {
    final var orderId = orderCreatorHandler.handle(items.toCommand());
    return buildResponse(orderId);
  }

  private ResponseEntity<OrderCreatedJson> buildResponse(OrderId orderId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new OrderCreatedJson(orderId.getValue()));
  }

  @Data
  static class OrderItemsJson {
    private String clientId;
    private List<ItemJson> items;

    CreateOrderWithItemsCommand toCommand() {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(it.price)))
        .collect(Collectors.toList());
      return CreateOrderWithItemsCommand.of(ClientId.of(clientId), itemsToAdd);
    }
  }

  @Data
  static class ItemJson {
    String productId;
    String price;
  }

  @Data
  @AllArgsConstructor
  static class OrderCreatedJson {
    String orderId;
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
