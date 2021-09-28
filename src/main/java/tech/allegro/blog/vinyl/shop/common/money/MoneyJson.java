package tech.allegro.blog.vinyl.shop.common.money;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record MoneyJson(String amount,
                        String currency) {

  @JsonIgnore
  public Money toDomain() {
    return Money.of(amount, currency);
  }
}
