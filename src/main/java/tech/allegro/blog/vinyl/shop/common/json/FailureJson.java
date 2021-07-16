package tech.allegro.blog.vinyl.shop.common.json;

public record FailureJson(String message, String details) {

  public FailureJson(String message) {
    this(message, "");
  }
}
