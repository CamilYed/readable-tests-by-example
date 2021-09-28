package tech.allegro.blog.vinyl.shop.common.volume;

public record Quantity(int value) {

  public static final Quantity ONE = new Quantity(1);

  public Quantity(int value) {
    if (value < 1) {
      throw new IllegalArgumentException("Quantity should be positive!");
    }
    this.value = value;
  }

  public Quantity add(Quantity quantity) {
    return new Quantity(quantity.value + this.value);
  }
}
