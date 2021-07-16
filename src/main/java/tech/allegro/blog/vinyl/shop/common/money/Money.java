package tech.allegro.blog.vinyl.shop.common.money;

import java.math.BigDecimal;
import java.util.Currency;

public record Money(BigDecimal value, Currency currency) {

  public static final Money ZERO = new Money(BigDecimal.valueOf(0));

  public Money(Double value) {
    this(BigDecimal.valueOf(value), Currency.getInstance("EUR"));
  }
  public Money(BigDecimal value) {
    this(value, Currency.getInstance("EUR"));
  }

  public Money add(Money money) {
    return new Money(this.value.add(money.value));
  }

  public boolean notEqualTo(Money money) {
    return !equalTo(money);
  }

  public boolean equalTo(Money money) {
    return this.value.compareTo(money.value) == 0;
  }

  public boolean greaterThan(Money money) {
    return this.value.compareTo(money.value) > 0;
  }
}
