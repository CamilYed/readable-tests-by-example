package tech.allegro.blog.vinyl.shop.order.api;

import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface Jsons {

  record CreateOrderJson(
    @NotBlank String clientId,
    List<@NotNull ItemCostAndQuantityJson> items
  ) {
  }

  record ChangeOrderItemQuantityJson(
    @NotBlank String productId,
    @Min(1) int quantity
  ) {
  }

  record PayOrderJson(@NotNull MoneyJson cost) {
  }

  record OrderCreatedResponseJson(
    String orderId,
    String clientId,
    List<ItemCostAndQuantityJson> items) {
  }

  record ItemCostAndQuantityJson(
    @NotNull ItemUnitPrice itemUnitPrice,
    @Min(1) int quantity) {

    public static record ItemUnitPrice(
      @NotBlank String productId,
      @NotNull MoneyJson price
    ) {
    }
  }
}
