package tech.allegro.blog.vinyl.shop.common.money;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value(staticConstructor = "of")
public class Money {
  BigDecimal value;
  Currency currency;

  public static final Currency EURO = Currency.getInstance("EUR");
  public static final Money ZERO = new Money(BigDecimal.valueOf(0), EURO);

  public static Money euro(String value) {
    return Money.of(new BigDecimal(value), EURO);
  }

  public static Money of(String value, String currency) {
    return Money.of(new BigDecimal(value), Currency.getInstance(currency));
  }

  public Money add(Money money) {
    if (money.currency.equals(this.currency)) {
      return Money.of(this.value.add(money.value), this.currency);
    } else throw new IllegalArgumentException("Can not add different currency");
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

  public boolean greaterOrEqualTo(Money money) {
    return this.value.compareTo(money.value) >= 0;
  }
}
