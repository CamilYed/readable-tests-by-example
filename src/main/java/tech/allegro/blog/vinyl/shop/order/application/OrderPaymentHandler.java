package tech.allegro.blog.vinyl.shop.order.application;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.result.Result;
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderDomainEvent;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseAlreadyPaid;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPayFailedBecauseDifferentAmounts;
import tech.allegro.blog.vinyl.shop.order.domain.Order;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler {
  private final OrderRepository orderRepository;
  private final OrderFactory orderFactory;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;
  private final DomainEventPublisher eventPublisher;

  public Result<Void> handle(PayOrderCommand command) {
    log.info("Start handling the command: {}", command);
    return Result.of(() -> {
      final var order = findOrderOrThrowNotFound(command.orderId);
      final var clientReputation = clientReputationProvider.get(command.clientId());
      final var delivery = calculateDeliveryCost(order.orderValue(), clientReputation);
      final var event = order.pay(command.amount, delivery);
      saveAndPublishWhenSucceeded(order, event);
    });
  }

  private Order findOrderOrThrowNotFound(OrderId orderId) {
    final var snapshot = orderRepository.findBy(orderId).orElseThrow(() -> OrderNotFound.of(orderId));
    return orderFactory.fromSnapshot(snapshot);
  }

  private Delivery calculateDeliveryCost(Money orderValue, ClientReputation clientReputation) {
    return deliveryCostPolicy.calculate(orderValue, clientReputation);
  }

  public record PayOrderCommand(ClientId clientId,
                                OrderId orderId,
                                Money amount) {

  }

  @Value(staticConstructor = "of")
  @EqualsAndHashCode(callSuper = true)
  public static class OrderNotFound extends RuntimeException {
    OrderId orderId;
  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class OrderAlreadyPaid extends RuntimeException {
    OrderId orderId;
  }

  @Value
  @EqualsAndHashCode(callSuper = true)
  public static class IncorrectAmount extends RuntimeException {
    OrderId orderId;
    Money difference;
  }

  private void saveAndPublishWhenSucceeded(Order order, OrderDomainEvent event) {
    switch (event) {
      case OrderPayFailedBecauseAlreadyPaid e -> {
        log.error("Order pay failed because already paid orderId = {}", e.orderId());
        throw new OrderAlreadyPaid(e.orderId());
      }
      case OrderPayFailedBecauseDifferentAmounts e -> {
        String message = "Order pay failed because amount is different than order value. OrderId = {} difference =  {}";
        log.error(message, e.orderId(), e.difference());
        throw new IncorrectAmount(e.orderId(), e.difference());
      }
      case OrderPaid paymentSucceeded -> {
        log.info("Payment succeeded order = {}", order.toSnapshot());
        orderRepository.save(order.toSnapshot());
        eventPublisher.publish(paymentSucceeded);
      }
    }
  }
}
