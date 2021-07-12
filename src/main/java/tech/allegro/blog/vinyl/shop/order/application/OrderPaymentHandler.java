package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.ClientId;
import tech.allegro.blog.vinyl.shop.client.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.commands.Result;
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
  public void handle(PayOrderCommand command) {
    final var result = Result.run(() -> {
      final var clientOrder = orderRepository.findBy(command.orderId);
      return clientOrder.map(order -> {
        var orderValue = order.orderValue();
        var clientReputation = clientReputationProvider.getFor(command.clientId);
        var delivery = deliveryCostPolicy.calculate(orderValue, clientReputation);
        return order.pay(command.amount, delivery);
      });
    });
    result.throwErrorIfOccurred();
  }

  public record PayOrderCommand(
    ClientId clientId,
    OrderId orderId,
    Money amount
  ) {
  }
}
