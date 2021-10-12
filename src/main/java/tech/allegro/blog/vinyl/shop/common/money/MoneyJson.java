package tech.allegro.blog.vinyl.shop.common.money;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotBlank;

public record MoneyJson(
  @NotBlank String amount,
  @NotBlank String currency
) {

  @JsonIgnore
  public Money toDomain() {
    return Money.of(amount, currency);
  }
}
