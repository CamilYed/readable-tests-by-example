package tech.allegro.blog.vinyl.shop.order.api;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.CreateOrderJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemCostAndQuantityJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemCostAndQuantityJson.ItemUnitPrice;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.OrderCreatedResponseJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequiredArgsConstructor
@ExtensionMethod({JsonsExtensions.class})
class OrderCreationEndpoint {
  private final OrderCreatorHandler orderCreatorHandler;

  @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> create(@Valid @RequestBody CreateOrderJson items) {
    final var resultOrError = orderCreatorHandler.handle(items.toCommand());
    final var success = resultOrError.getSuccessOrThrowError();
    return buildResponse(success);
  }

  @PutMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedResponseJson> upsert(@NotBlank @PathVariable("orderId") String orderId,
                                                  @Valid @RequestBody CreateOrderJson items) {
    final var resultOrError = orderCreatorHandler.handle(items.toCommand(orderId));
    final var createdOrderSnapshot = resultOrError.getSuccessOrThrowError();
    return buildResponse(createdOrderSnapshot);
  }

  private ResponseEntity<OrderCreatedResponseJson> buildResponse(OrderDataSnapshot data) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
      new OrderCreatedResponseJson(
        data.orderId().value(),
        data.clientId().value(),
        data.items().entrySet().stream()
          .map(it ->
            new ItemCostAndQuantityJson(
              new ItemUnitPrice(
                it.getKey().vinylId().value(),
                new MoneyJson(it.getKey().unitPrice().value().toString(), it.getKey().unitPrice().currency().getCurrencyCode())
              ),
              it.getValue().value()
            )
          ).collect(toList())
      )
    );
  }

//  @ExceptionHandler(Throwable.class)
//  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
//    log.error("An unexpected error occurred during order modification", e);
//    FailureJson errorMessage = new FailureJson(e.getMessage());
//    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
//  }
}
