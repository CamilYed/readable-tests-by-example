package tech.allegro.blog.vinyl.shop.order.application;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler {
  private final OrderRepository orderRepository;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;
  private final DomainEventPublisher domainEventPublisher;

  public void handle(PayOrderCommand command) {
    final var order = orderRepository.findBy(command.orderId).orElseThrow(OrderNotFound::new);
    final var clientReputation = clientReputationProvider.get(command.getClientId());
    final var delivery = calculateDeliveryCost(order.orderValue(), clientReputation);
    final var event = order.pay(command.amount, delivery);
    orderRepository.save(order);
    domainEventPublisher.publish(event);
  }

  private Delivery calculateDeliveryCost(Money orderValue, ClientReputation clientReputation) {
    return deliveryCostPolicy.calculate(orderValue, clientReputation);
  }

  @Value(staticConstructor = "of")
  public static class PayOrderCommand {
    ClientId clientId;
    OrderId orderId;
    Money amount;
  }

  static class OrderNotFound extends RuntimeException {

  }
}
