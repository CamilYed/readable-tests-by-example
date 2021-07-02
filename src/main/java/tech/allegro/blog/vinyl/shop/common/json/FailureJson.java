package tech.allegro.blog.vinyl.shop.common.json;

public record FailureJson(Integer code, String message, String details) {

  public FailureJson(Integer code, String message) {
    this(code, message, "");
  }
}
