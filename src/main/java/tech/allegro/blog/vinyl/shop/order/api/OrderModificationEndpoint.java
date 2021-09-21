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

  static class OrderItemsJson {
    private final List<ItemJson> items;

    public OrderItemsJson(List<ItemJson> items) {
      this.items = items;
    }

    AddItemsToOrderCommand toCommand(String orderId) {
      final var itemsToAdd = items.stream()
        .map(it -> Item.of(VinylId.of(it.productId), Money.of(it.cost.getAmount(), it.cost.getCurrency())))
        .collect(Collectors.toList());
      return AddItemsToOrderCommand.of(OrderId.of(orderId), itemsToAdd);
    }

    public List<ItemJson> getItems() {
      return this.items;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof OrderItemsJson))
        return false;
      final OrderItemsJson other = (OrderItemsJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$items = this.getItems();
      final Object other$items = other.getItems();
      if (this$items == null ? other$items != null : !this$items.equals(other$items)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof OrderItemsJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $items = this.getItems();
      result = result * PRIME + ($items == null ? 43 : $items.hashCode());
      return result;
    }

    public String toString() {
      return "OrderModificationEndpoint.OrderItemsJson(items=" + this.getItems() + ")";
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
      return "OrderModificationEndpoint.ItemJson(productId=" + this.getProductId() + ", cost=" + this.getCost() + ")";
    }
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during order modification", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
