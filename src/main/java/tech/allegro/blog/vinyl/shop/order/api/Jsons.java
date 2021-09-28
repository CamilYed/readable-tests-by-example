package tech.allegro.blog.vinyl.shop.order.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderWithIdCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.Values;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Jsons {

  record CreateOrderJson(String clientId,
                         List<ItemAndQuantityJson> items) {

    @JsonIgnore
    public CreateOrderCommand toCommand() {
      final var itemsToAdd = mapItemsJsonToDomain(items);
      return new CreateOrderCommand(new ClientId(clientId), itemsToAdd);
    }
  }

  record CreateOrderWithIdJson(
    String orderId,
    String clientId,
    List<ItemAndQuantityJson> items
  ) {

    @JsonIgnore
    public CreateOrderWithIdCommand toCommand() {
      final var itemsToAdd = mapItemsJsonToDomain(items);
      return new CreateOrderWithIdCommand(new Values.OrderId(orderId), new ClientId(clientId), itemsToAdd);
    }
  }

  record OrderItemsJson(List<ItemAndQuantityJson> items) {

    @JsonIgnore
    OrderModificationHandler.AddItemsToOrderCommand toCommand(String orderId) {
      return new OrderModificationHandler.AddItemsToOrderCommand(new Values.OrderId(orderId), mapItemsJsonToDomain(items));
    }
  }

  record ItemAndQuantityJson(Item item,
                             Quantity quantity) {
    @JsonIgnore
    Vinyl toDomain() {
      return new Vinyl(new VinylId(this.item.productId), item.cost.toDomain());
    }

    public static record Item(String productId,
                              MoneyJson cost) {
    }

    public static record Quantity(int value) {
    }
  }

  record PayOrderJson(String clientId,
                      MoneyJson cost) {

    @JsonIgnore
    public PayOrderCommand toCommand(String orderId) {
      return new PayOrderCommand(
        new ClientId(clientId),
        new Values.OrderId(orderId),
        Money.of(cost.amount(), cost.currency())
      );
    }
  }

  record OrderCreatedResponseJson(String orderId) {
  }

  private static Map<Vinyl, Quantity> mapItemsJsonToDomain(List<ItemAndQuantityJson> items) {
    return items.stream()
      .collect(Collectors.toMap(ItemAndQuantityJson::toDomain, e -> new Quantity(e.quantity().value())));
  }
}
