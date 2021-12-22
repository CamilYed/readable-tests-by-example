package tech.allegro.blog.vinyl.shop.common.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher.SpringDomainEventPublisher;

@Configuration
class DomainEventPublisherConfig {

  @Bean
  DomainEventPublisher domainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    return new SpringDomainEventPublisher(applicationEventPublisher);
  }
}
