package tech.allegro.blog.vinyl.shop.order.api;

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl;
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.volume.Quantity;
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ChangeOrderItemQuantityJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.ItemCostAndQuantityJson;
import tech.allegro.blog.vinyl.shop.order.api.Jsons.PayOrderJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderCreatorHandler.CreateOrderWithIdCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.ChangeItemQuantityCommand;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class JsonsExtensions {

  public static CreateOrderCommand toCommand(Jsons.CreateOrderJson json) {
    final var itemsToAdd = mapItemsJsonToDomainForCreate(json.items());
    return new CreateOrderCommand(new ClientId(json.clientId()), itemsToAdd);
  }

  public static CreateOrderWithIdCommand toCommand(Jsons.CreateOrderJson json, String orderId) {
    final var itemsToAdd = mapItemsJsonToDomainForCreate(json.items());
    return new CreateOrderWithIdCommand(new OrderId(orderId), new ClientId(json.clientId()), itemsToAdd);
  }

  public static ChangeItemQuantityCommand toCommand(ChangeOrderItemQuantityJson json, String productId, String orderId) {
    if (!productId.equals(json.productId())) {
      throw new ConstraintViolationException("Inconsistency between productId url param and body", null);
    }
    return new ChangeItemQuantityCommand(
      new OrderId(orderId),
      new VinylId(json.productId()),
      new QuantityChange(json.quantity())
    );
  }

  public static PayOrderCommand toCommand(PayOrderJson json, String orderId) {
    return new PayOrderCommand(
      new OrderId(orderId),
      Money.of(json.cost().amount(), json.cost().currency())
    );
  }

  private static Map<Vinyl, Quantity> mapItemsJsonToDomainForCreate(List<ItemCostAndQuantityJson> items) {
    return items.stream()
      .collect(Collectors.toMap(JsonsExtensions::toDomain, e -> new Quantity(e.quantity())));
  }

  private static Vinyl toDomain(ItemCostAndQuantityJson json) {
    return new Vinyl(new VinylId(json.itemUnitCost().productId()), json.itemUnitCost().cost().toDomain());
  }
}
