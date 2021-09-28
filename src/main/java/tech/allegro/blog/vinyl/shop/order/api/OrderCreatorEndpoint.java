package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.CreateOrderJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.CreateOrderWithIdJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemAndQuantityJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemAndQuantityJson.Item;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemAndQuantityJson.Quantity;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.OrderCreatedResponseJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.domain.Values;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
class OrderCreatorEndpoint {
  private final OrderCreatorHandler orderCreatorHandler;

  @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> create(@RequestBody CreateOrderJson items) {
    final var resultOrError = orderCreatorHandler.handle(items.toCommand());
    final var success = resultOrError.getSuccessOrThrowError();
    return buildResponse(success);
  }

  @PutMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> upsert(@PathVariable("orderId") String orderId,
                                                  @RequestBody CreateOrderWithIdJson items) {
    final var resultOrError = orderCreatorHandler.handle(items.toCommand());
    final var success = resultOrError.getSuccessOrThrowError();
    return buildResponse(success);
  }

  private ResponseEntity<OrderCreatedResponseJson> buildResponse(Values.OrderDataSnapshot data) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new OrderCreatedResponseJson(
        data.orderId().value(),
        data.clientId().value(),
        data.items().entrySet().stream()
          .map(it ->
            new ItemAndQuantityJson(
              new Item(
                it.getKey().vinylId().value(),
                new MoneyJson(it.getKey().price().value().toString(), it.getKey().price().currency().getCurrencyCode())
              ),
              new Quantity(it.getValue().value())
            )
          ).collect(toList())
      )
    );
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
