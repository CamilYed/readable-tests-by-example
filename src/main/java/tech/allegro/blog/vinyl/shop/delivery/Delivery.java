package tech.allegro.blog.vinyl.shop.delivery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;

@RequiredArgsConstructor
public abstract class Delivery {
  @Getter private final Money cost;

  public static FreeDeliveryDueToOrderCost freeDeliveryDueToOrderCost() {
    return new FreeDeliveryDueToOrderCost();
  }

  public static FreeDeliveryDueToClientReputation freeDeliveryDueToClientReputation() {
    return new FreeDeliveryDueToClientReputation();
  }

  public static StandardDelivery standardDelivery(Money cost) {
    return new StandardDelivery(cost);
  }

  public static StandardDelivery standardDeliveryWithDefaultPrice() {
    return new StandardDelivery(Money.of("20.00"));
  }

  public static final class FreeDeliveryDueToOrderCost extends Delivery {
    public FreeDeliveryDueToOrderCost() {
      super(Money.ZERO);
    }
  }

  public static final class FreeDeliveryDueToClientReputation extends Delivery {
    public FreeDeliveryDueToClientReputation() {
      super(Money.ZERO);
    }
  }

  public static final class StandardDelivery extends Delivery {
    public StandardDelivery(Money cost) {
      super(cost);
    }
  }
}

