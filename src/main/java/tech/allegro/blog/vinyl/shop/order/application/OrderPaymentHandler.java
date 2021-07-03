package tech.allegro.blog.vinyl.shop.order.application;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.ClientId;
import tech.allegro.blog.vinyl.shop.client.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler implements CommandHandler<OrderPaymentHandler.PayOrderCommand> {
  private final OrderRepository orderRepository;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;

  @Override
  public Either<Failure, Void> handle(PayOrderCommand command) {
    Try.run(() -> {
        final var clientOrder = orderRepository.findBy(command.orderId);
        clientOrder.ifPresent(order -> {
          var orderValue = order.orderValue();
          var clientReputation = clientReputationProvider.getFor(command.clientId);
          var delivery = deliveryCostPolicy.calculate(orderValue, clientReputation);
          var paymentResult = order.pay(command.amount, delivery);
          orderRepository.save(order);
        });
      }
    ).onFailure(throwable -> {
      log.error("There was an error trying to pay for order {}", command.orderId);
    });

    return null;
  }

  public record PayOrderCommand(
    ClientId clientId,
    OrderId orderId,
    Money amount
  ) {
  }
}
