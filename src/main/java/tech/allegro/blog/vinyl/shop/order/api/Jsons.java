package tech.allegro.blog.vinyl.shop.order.api;

import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface Jsons {

  record CreateOrderJson(
    @NotBlank String clientId,
    List<@NotNull @Valid ItemCostAndQuantityJson> items
  ) {
  }

  record ChangeOrderItemQuantityJson(
    @NotBlank String productId,
    @Min(1) int quantity
  ) {
  }

  record PayOrderJson(@NotNull @Valid MoneyJson cost) {
  }

  record OrderCreatedResponseJson(
    String orderId,
    String clientId,
    List<ItemCostAndQuantityJson> items) {
  }

  record ItemCostAndQuantityJson(
    @NotNull @Valid ItemUnitPrice itemUnitPrice,
    @Min(1) int quantity) {

    public static record ItemUnitPrice(
      @NotBlank String productId,
      @NotNull @Valid MoneyJson price
    ) {
    }
  }
}
