package tech.allegro.blog.vinyl.shop.order.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Configuration
class OrderHandlersConfig {

  @Bean
  OrderCreatorHandler orderCreatorHandler(OrderRepository orderRepository) {
    return new OrderCreatorHandler(new OrderFactory(), orderRepository);
  }

  @Bean
  OrderModificationHandler orderModificationHandler(OrderRepository orderRepository) {
    return new OrderModificationHandler(orderRepository, new OrderFactory());
  }

  @Bean
  OrderPaymentHandler orderPaymentHandler(
    OrderRepository orderRepository,
    ClientReputationProvider clientReputationProvider,
    DeliveryCostPolicy deliveryCostPolicy,
    DomainEventPublisher domainEventPublisher
  ) {
    return new OrderPaymentHandler(
      orderRepository,
      new OrderFactory(),
      clientReputationProvider,
      deliveryCostPolicy,
      domainEventPublisher);
  }
}
