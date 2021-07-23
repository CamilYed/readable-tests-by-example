package tech.allegro.blog.vinyl.shop.common.money;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value(staticConstructor = "of")
public class Money {
  BigDecimal value;
  Currency currency = EURO;

  public static final Currency EURO = Currency.getInstance("EUR");
  public static final Money ZERO = new Money(BigDecimal.valueOf(0));

  public static Money of(Double value) {
    return Money.of(BigDecimal.valueOf(value));
  }

  public Money add(Money money) {
    return Money.of(this.value.add(money.value));
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
