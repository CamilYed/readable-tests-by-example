package tech.allegro.blog.vinyl.shop.common.commands;

import io.vavr.control.Either;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;

public interface CommandHandler<T> {

  Either<Failure, Void> handle(T command);

  record Failure(Integer code, String message) {

    public FailureJson toJson() {
      return new FailureJson(code, message);
    }
  }
}
