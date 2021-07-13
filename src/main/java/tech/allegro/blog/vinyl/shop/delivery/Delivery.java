package tech.allegro.blog.vinyl.shop.delivery;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.money.Money;

@RequiredArgsConstructor
public sealed class Delivery {
  private final DeliveryId id;
  @Getter private final Money cost;

  public static FreeDeliveryDueToOrderCost freeDeliveryDueToOrderCost() {
    return new FreeDeliveryDueToOrderCost(DeliveryId.random());
  }

  public static FreeDeliveryDueToClientReputation freeDeliveryDueToClientReputation() {
    return new FreeDeliveryDueToClientReputation(DeliveryId.random());
  }

  public static StandardDelivery standardDelivery(Money cost) {
    return new StandardDelivery(DeliveryId.random(), cost);
  }

  public static StandardDelivery standardDeliveryWithDefaultPrice() {
    return new StandardDelivery(DeliveryId.random(), new Money(20.00));
  }

  public static final class FreeDeliveryDueToOrderCost extends Delivery {
    public FreeDeliveryDueToOrderCost(DeliveryId id) {
      super(id, Money.ZERO);
    }
  }

  public static final class FreeDeliveryDueToClientReputation extends Delivery {
    public FreeDeliveryDueToClientReputation(DeliveryId id) {
      super(id, Money.ZERO);
    }
  }

  public static final class StandardDelivery extends Delivery {
    public StandardDelivery(DeliveryId id, Money cost) {
      super(id, cost);
    }
  }
}

