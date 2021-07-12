package tech.allegro.blog.vinyl.shop.common.commands;

public interface CommandHandler<T> {

  void handle(T command);
}
