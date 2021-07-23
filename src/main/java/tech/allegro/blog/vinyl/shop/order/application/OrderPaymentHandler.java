package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.ClientId;
import tech.allegro.blog.vinyl.shop.client.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;
import tech.allegro.blog.vinyl.shop.delivery.Delivery.FreeDeliveryDueToClientReputation;
import tech.allegro.blog.vinyl.shop.delivery.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.adapters.MailBoxSystemBox;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent.OrderPaidEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.Order.AmountToPayIsDifferent;
import tech.allegro.blog.vinyl.shop.order.domain.Order.OrderAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.OrderId;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler implements CommandHandler<OrderPaymentHandler.PayOrderCommand> {
  private final OrderRepository orderRepository;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;
  private final DomainEventPublisher domainEventPublisher;
  private final MailBoxSystemBox mailBoxSystemBox;

  @Override
  public void handle(PayOrderCommand command) {
    final var clientOrder = orderRepository.findBy(command.orderId);
    clientOrder.ifPresent(order -> {
      Delivery delivery = calculateDeliveryCost(command, order);
      final var event = order.pay(command.amount, delivery);
      domainEventPublisher.saveAndPublish(event);
      sendFreeTrackMusicForVipClient(command.clientId, event);
    });
  }

  private Delivery calculateDeliveryCost(PayOrderCommand command, Order order) {
    var orderValue = order.orderValue();
    var clientReputation = clientReputationProvider.getFor(command.getClientId());
    return deliveryCostPolicy.calculate(orderValue, clientReputation);
  }

  private void sendFreeTrackMusicForVipClient(ClientId clientId, OrderPaidEvent event) {
    if (shouldSendAlsoFreeTrackMusic(event)) {
      mailBoxSystemBox.sendFreeMusicTrackForClient(clientId);
    }
  }

  private boolean shouldSendAlsoFreeTrackMusic(OrderPaidEvent it) {
    return it.getDelivery() instanceof FreeDeliveryDueToClientReputation;
  }

  @Value(staticConstructor = "of")
  static public class PayOrderCommand {
    ClientId clientId;
    OrderId orderId;
    Money amount;
  }
}
