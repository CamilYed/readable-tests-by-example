package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.FreeMusicTrackSender;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler implements CommandHandler<OrderPaymentHandler.PayOrderCommand> {
  private final OrderRepository orderRepository;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;
  private final DomainEventPublisher domainEventPublisher;
  private final FreeMusicTrackSender freeMusicTrackSender;

  @Override
  public void handle(PayOrderCommand command) {
    final var clientOrder = orderRepository.findBy(command.orderId);
    clientOrder.ifPresent(order -> {
      final var clientReputation = clientReputationProvider.get(command.getClientId());
      final var delivery = calculateDeliveryCost(order.orderValue(), clientReputation);
      final var event = order.pay(command.amount, delivery);
      domainEventPublisher.publish(event);
      sendFreeTrackMusicForVipClient(clientReputation);
    });
  }

  private Delivery calculateDeliveryCost(Money orderValue, ClientReputation clientReputation) {
    return deliveryCostPolicy.calculate(orderValue, clientReputation);
  }

  private void sendFreeTrackMusicForVipClient(ClientReputation clientReputation) {
    if (clientReputation.isVip()) {
      freeMusicTrackSender.send(clientReputation.getClientId());
    }
  }

  @Value(staticConstructor = "of")
  static public class PayOrderCommand {
    ClientId clientId;
    OrderId orderId;
    Money amount;
  }
}
