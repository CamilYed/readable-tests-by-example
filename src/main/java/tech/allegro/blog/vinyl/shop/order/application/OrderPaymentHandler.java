package tech.allegro.blog.vinyl.shop.order.application;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
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
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@Slf4j
@RequiredArgsConstructor
public class OrderPaymentHandler {
  private final OrderRepository orderRepository;
  private final OrderFactory orderFactory;
  private final ClientReputationProvider clientReputationProvider;
  private final DeliveryCostPolicy deliveryCostPolicy;
  private final DomainEventPublisher eventPublisher;

  // Now we only have the InMemory repository, so we don't need the @Transactional annotation here
  public Result<Void> handle(PayOrderCommand command) {
    log.info("Start handling the command: {}", command);
    return Result.of(() -> {
      final var orderSnapshot = findOrderOrThrowNotFound(command.orderId);
      final var delivery = calculateDeliveryCost(orderSnapshot);
      final var order = orderFactory.fromSnapshot(orderSnapshot);
      final var event = order.pay(command.amount, delivery);
      saveAndPublishWhenSucceeded(order, event);
    });
  }

  private OrderDataSnapshot findOrderOrThrowNotFound(OrderId orderId) {
    return orderRepository.findBy(orderId).orElseThrow(() -> new OrderNotFound(orderId));
  }

  private Delivery calculateDeliveryCost(OrderDataSnapshot order) {
    final var clientReputation = clientReputationProvider.get(order.clientId());
    return deliveryCostPolicy.calculate(order.cost(), clientReputation);
  }

  public record PayOrderCommand(
    OrderId orderId,
    Money amount) {
  }

  @Value
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
      default -> log.warn("Unhandled use case event = {}", event);
    }
  }
}
