package tech.allegro.blog.vinyl.shop.delivery.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;

@RequiredArgsConstructor
public abstract class Delivery {
  @Getter
  private final Money cost;

  public static FreeDelivery freeDelivery() {
    return new FreeDelivery();
  }

  public static StandardDelivery standardDelivery(Money cost) {
    return new StandardDelivery(cost);
  }

  public static StandardDelivery standardDeliveryWithDefaultPrice() {
    return new StandardDelivery(Money.of("20.00"));
  }

  public static final class FreeDelivery extends Delivery {
    public FreeDelivery() {
      super(Money.ZERO);
    }
  }

  public static final class StandardDelivery extends Delivery {
    public StandardDelivery(Money cost) {
      super(cost);
    }
  }
}

