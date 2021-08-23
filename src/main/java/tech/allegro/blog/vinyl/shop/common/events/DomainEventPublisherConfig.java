package tech.allegro.blog.vinyl.shop.common.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent;

@Configuration
class DomainEventPublisherConfig {

  @Bean
  DomainEventPublisher domainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    return new SpringDomainEventPublisher(applicationEventPublisher);
  }

  @RequiredArgsConstructor
  static class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
      applicationEventPublisher.publishEvent(event);
    }
  }
}
