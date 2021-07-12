package tech.allegro.blog.vinyl.shop.common.commands;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Result<SUCCESS> {

  private final SUCCESS success;
  private final Failure error;

  public static <SUCCESS> Result<SUCCESS> run(Supplier<SUCCESS> block) {
    try {
      return new Result<>(block.get(), null);
    } catch (Throwable t) {
      return new Result<SUCCESS>(block.get(), new Failure(t));
    }
  }

  public boolean isSuccess() {
    return success != null;
  }

  public void throwErrorIfOccurred() {
    if (!isSuccess())
      throw new Failure(error);
  }

  public static class Failure extends RuntimeException {

    public Failure(Throwable cause) {
      super(cause);
    }
  }
}
