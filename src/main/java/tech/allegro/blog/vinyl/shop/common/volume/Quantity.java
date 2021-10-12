package tech.allegro.blog.vinyl.shop.common.volume;

public record Quantity(int value) {

  public static final Quantity ONE = new Quantity(1);

  public Quantity {
    if (value < 1) {
      throw new IllegalArgumentException("Quantity must be positive!");
    }
  }

  public Quantity change(QuantityChange quantityChange) {
    final int newValue =  quantityChange.value();
    if (newValue < 1)
      return ONE;
    return new Quantity(newValue);
  }

  public Quantity add(Quantity quantity) {
    return new Quantity(quantity.value + this.value);
  }
}
