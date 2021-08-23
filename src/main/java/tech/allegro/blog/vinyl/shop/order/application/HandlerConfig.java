package tech.allegro.blog.vinyl.shop.order.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostPolicy;
import tech.allegro.blog.vinyl.shop.order.domain.FreeMusicTrackSender;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Configuration
class HandlerConfig {

  @Bean
  OrderModificationHandler orderModificationHandler(OrderRepository orderRepository) {
    return new OrderModificationHandler(orderRepository);
  }

  @Bean
  OrderPaymentHandler orderPaymentHandler(
    OrderRepository orderRepository,
    ClientReputationProvider clientReputationProvider,
    DeliveryCostPolicy deliveryCostPolicy,
    DomainEventPublisher domainEventPublisher,
    FreeMusicTrackSender mailBoxSystemBox
  ) {
    return new OrderPaymentHandler(orderRepository, clientReputationProvider, deliveryCostPolicy, domainEventPublisher, mailBoxSystemBox);
  }
}
