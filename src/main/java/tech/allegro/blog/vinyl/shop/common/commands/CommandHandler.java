package tech.allegro.blog.vinyl.shop.common.commands;

public interface CommandHandler<T> {

  void handle(T command);

//  @Value(staticConstructor = "of")
//  class Result<R> {
//    R r;
//
//    public static <R> Result<R> nothing() {
//      return new Result<>(null);
//    }
//  }
}
