package tech.allegro.blog.vinyl.shop.common.result;


import java.util.function.Supplier;

public record Result<SUCCESS>(SUCCESS success, Error error) {

  public static <SUCCESS> Result<SUCCESS> of(Supplier<SUCCESS> block) {
    try {
      return new Result<>(block.get(), null);
    } catch (Throwable ex) {
      return new Result<>(null, new Error(ex));
    }
  }

  public static Result<Void> of(CheckedRunnable runnable) {
    try {
      runnable.run();
      return new Result<>(null, null);
    } catch (Throwable ex) {
      return new Result<>(null, new Error(ex));
    }
  }

  public boolean isSuccess() {
    return !isError();
  }

  public boolean isError() {
    return error != null;
  }

  public SUCCESS getSuccessOrDefault(SUCCESS defaultV) {
    if (isError())
      return defaultV;
    return success;
  }

  public record Error(Throwable cause) {
  }

  @FunctionalInterface
  public interface CheckedRunnable {
    void run() throws Throwable;
  }
}
