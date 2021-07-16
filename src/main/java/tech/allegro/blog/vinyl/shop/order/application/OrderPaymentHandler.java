package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.ClientId;
import tech.allegro.blog.vinyl.shop.client.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.commands.CommandHandler;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.Delivery;
import tech.allegro.blog.vinyl.shop.delivery.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.adapters.MailBoxSystemBox;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
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
    final var paymentResult = clientOrder.map(order -> {
      var orderValue = order.orderValue();
      var clientReputation = clientReputationProvider.getFor(command.clientId);
      var delivery = deliveryCostPolicy.calculate(orderValue, clientReputation);
      return tryPayOrderWithDelivery(command.amount, order, delivery);
    });

    paymentResult.ifPresent(domainEventPublisher::saveAndPublish);
    paymentResult.ifPresent(it -> {
      domainEventPublisher.saveAndPublish(it);
      if (shouldSendAlsoFreeTrackMusic(it)) {
        mailBoxSystemBox.sendFreeMusicTrackForClient(command.clientId);
      }
    });
  }

  private DomainEvent.OrderPaidEvent tryPayOrderWithDelivery(Money amount, Order order, Delivery delivery) {
    try {
      return order.pay(amount, delivery);
    } catch (Order.AmountToPayIsDifferent | Order.OrderAlreadyPaid e) {
        log.error("Can not pay order", e);
        // TODO map to another exc.
        throw e;
    }
  }

  private boolean shouldSendAlsoFreeTrackMusic(DomainEvent.OrderPaidEvent it) {
    return it.delivery() instanceof Delivery.FreeDeliveryDueToClientReputation;
  }

  public record PayOrderCommand(
    ClientId clientId,
    OrderId orderId,
    Money amount
  ) {
  }
}
