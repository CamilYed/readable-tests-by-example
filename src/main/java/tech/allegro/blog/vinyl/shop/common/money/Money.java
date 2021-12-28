package tech.allegro.blog.vinyl.shop.common.money;

import tech.allegro.blog.vinyl.shop.common.volume.Quantity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal value, Currency currency) {
  public static final Currency EURO = Currency.getInstance("EUR");
  public static final Money ZERO = new Money(BigDecimal.valueOf(00.00), EURO);

  public static Money euro(Double value) {
    return of(value, "EUR");
  }

  public static Money of(String value, String currency) {
    return new Money(new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN), Currency.getInstance(currency));
  }

  public static Money of(Double value, String currency) {
    return new Money(new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN), Currency.getInstance(currency));
  }

  public Money add(Money money) {
    if (money.currency.equals(this.currency)) {
      return new Money(this.value.add(money.value), this.currency);
    } else throw new IllegalArgumentException("Can not add different currency");
  }

  public Money subtract(Money money) {
    if (money.currency.equals(this.currency)) {
      return new Money(this.value.subtract(money.value), this.currency);
    } else throw new IllegalArgumentException("Can not add different currency");
  }

  public Money multiply(Quantity quantity) {
    return new Money(this.value.multiply(BigDecimal.valueOf(quantity.value())), this.currency);
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
