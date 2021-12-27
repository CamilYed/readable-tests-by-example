package tech.allegro.blog.vinyl.shop.common.json;

import java.util.List;

public record ErrorsJson(
  List<Error> errors
) {
  static public record Error(
    String code,
    String message,
    String details,
    String path,
    String userMessage
  ) {

    public static ErrorJsonBuilder builder() {
      return new ErrorJsonBuilder();
    }

    public static class ErrorJsonBuilder {
      private String code;
      private String message;
      private String details;
      private String path;
      private String userMessage;

      ErrorJsonBuilder() {
      }

      public ErrorJsonBuilder withCode(String code) {
        this.code = code;
        return this;
      }

      public ErrorJsonBuilder withMessage(String message) {
        this.message = message;
        return this;
      }

      public ErrorJsonBuilder withDetails(String details) {
        this.details = details;
        return this;
      }

      public ErrorJsonBuilder withPath(String path) {
        this.path = path;
        return this;
      }

      public ErrorJsonBuilder withUserMessage(String userMessage) {
        this.userMessage = userMessage;
        return this;
      }

      public Error build() {
        return new Error(
          this.code,
          this.message,
          this.details,
          this.path,
          this.userMessage
        );
      }
    }
  }
}
