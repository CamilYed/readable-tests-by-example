package tech.allegro.blog.vinyl.shop.order.application;

import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@RequiredArgsConstructor
public class PaymentHandler implements CommandHandler<PaymentHandler.PayOrderCommand> {

  private final OrderRepository orderRepository;

  @Override
  public Either<Failure, Void> handle(PayOrderCommand command) {

    return null;
  }


  public record PayOrderCommand(
    OrderId orderId,
    Money amount
  ) {
  }
}
