package tech.allegro.blog.vinyl.shop.order.api;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderWithIdCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.Item;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class OrderCreatorEndpoint {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderCreatorEndpoint.class);
  private final OrderCreatorHandler orderCreatorHandler;

  public OrderCreatorEndpoint(OrderCreatorHandler orderCreatorHandler) {
    this.orderCreatorHandler = orderCreatorHandler;
  }

  @PostMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedJson> create(@RequestBody CreateOrderJson items) {
    final var orderId = orderCreatorHandler.handle(items.toCommand());
    return buildResponse(orderId);
  }

  @PutMapping(value = "/orders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<OrderCreatedJson> upsert(@PathVariable("orderId") String orderId,
                                          @RequestBody CreateOrderWithIdJson items) {
    orderCreatorHandler.handle(items.toCommand());
    return buildResponse(OrderId.of(orderId));
  }

  private ResponseEntity<OrderCreatedJson> buildResponse(OrderId orderId) {
    return ResponseEntity.status(HttpStatus.CREATED).body(new OrderCreatedJson(orderId.getValue()));
  }

  static class CreateOrderJson {
    String clientId;
    List<ItemJson> items;

    public CreateOrderJson() {
    }

    CreateOrderCommand toCommand() {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(it.cost.getAmount(), it.cost.getCurrency())))
        .collect(Collectors.toList());
      return CreateOrderCommand.of(ClientId.of(clientId), itemsToAdd);
    }

    public String getClientId() {
      return this.clientId;
    }

    public List<ItemJson> getItems() {
      return this.items;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public void setItems(List<ItemJson> items) {
      this.items = items;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof CreateOrderJson)) return false;
      final CreateOrderJson other = (CreateOrderJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$clientId = this.getClientId();
      final Object other$clientId = other.getClientId();
      if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) return false;
      final Object this$items = this.getItems();
      final Object other$items = other.getItems();
      if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof CreateOrderJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $clientId = this.getClientId();
      result = result * PRIME + ($clientId == null ? 43 : $clientId.hashCode());
      final Object $items = this.getItems();
      result = result * PRIME + ($items == null ? 43 : $items.hashCode());
      return result;
    }

    public String toString() {
      return "OrderCreatorEndpoint.CreateOrderJson(clientId=" + this.getClientId() + ", items=" + this.getItems() + ")";
    }
  }

  static class CreateOrderWithIdJson {
    String orderId;
    String clientId;
    List<ItemJson> items;

    public CreateOrderWithIdJson() {
    }

    CreateOrderWithIdCommand toCommand() {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(it.cost.getAmount(), it.cost.getCurrency())))
        .collect(Collectors.toList());
      return CreateOrderWithIdCommand.of(OrderId.of(orderId), ClientId.of(clientId), itemsToAdd);
    }

    public String getOrderId() {
      return this.orderId;
    }

    public String getClientId() {
      return this.clientId;
    }

    public List<ItemJson> getItems() {
      return this.items;
    }

    public void setOrderId(String orderId) {
      this.orderId = orderId;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public void setItems(List<ItemJson> items) {
      this.items = items;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof CreateOrderWithIdJson))
        return false;
      final CreateOrderWithIdJson other = (CreateOrderWithIdJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$orderId = this.getOrderId();
      final Object other$orderId = other.getOrderId();
      if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
      final Object this$clientId = this.getClientId();
      final Object other$clientId = other.getClientId();
      if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) return false;
      final Object this$items = this.getItems();
      final Object other$items = other.getItems();
      if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof CreateOrderWithIdJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $orderId = this.getOrderId();
      result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
      final Object $clientId = this.getClientId();
      result = result * PRIME + ($clientId == null ? 43 : $clientId.hashCode());
      final Object $items = this.getItems();
      result = result * PRIME + ($items == null ? 43 : $items.hashCode());
      return result;
    }

    public String toString() {
      return "OrderCreatorEndpoint.CreateOrderWithIdJson(orderId=" + this.getOrderId() + ", clientId=" + this.getClientId() + ", items=" + this.getItems() + ")";
    }
  }

  static class ItemJson {
    String productId;
    MoneyJson cost;

    public ItemJson() {
    }

    public String getProductId() {
      return this.productId;
    }

    public MoneyJson getCost() {
      return this.cost;
    }

    public void setProductId(String productId) {
      this.productId = productId;
    }

    public void setCost(MoneyJson cost) {
      this.cost = cost;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof ItemJson)) return false;
      final ItemJson other = (ItemJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$productId = this.getProductId();
      final Object other$productId = other.getProductId();
      if (this$productId == null ? other$productId != null : !this$productId.equals(other$productId)) return false;
      final Object this$cost = this.getCost();
      final Object other$cost = other.getCost();
      if (this$cost == null ? other$cost != null : !this$cost.equals(other$cost)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof ItemJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $productId = this.getProductId();
      result = result * PRIME + ($productId == null ? 43 : $productId.hashCode());
      final Object $cost = this.getCost();
      result = result * PRIME + ($cost == null ? 43 : $cost.hashCode());
      return result;
    }

    public String toString() {
      return "OrderCreatorEndpoint.ItemJson(productId=" + this.getProductId() + ", cost=" + this.getCost() + ")";
    }
  }

  static class OrderCreatedJson {
    String orderId;

    public OrderCreatedJson(String orderId) {
      this.orderId = orderId;
    }

    public String getOrderId() {
      return this.orderId;
    }

    public void setOrderId(String orderId) {
      this.orderId = orderId;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof OrderCreatedJson)) return false;
      final OrderCreatedJson other = (OrderCreatedJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$orderId = this.getOrderId();
      final Object other$orderId = other.getOrderId();
      if (this$orderId == null ? other$orderId != null : !this$orderId.equals(other$orderId)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof OrderCreatedJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $orderId = this.getOrderId();
      result = result * PRIME + ($orderId == null ? 43 : $orderId.hashCode());
      return result;
    }

    public String toString() {
      return "OrderCreatorEndpoint.OrderCreatedJson(orderId=" + this.getOrderId() + ")";
    }
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
