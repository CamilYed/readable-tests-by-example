package tech.allegro.blog.vinyl.shop.common.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

public interface DomainEventPublisher {

  void publish(DomainEvent event);

  @RequiredArgsConstructor
  class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
      applicationEventPublisher.publishEvent(event);
    }
  }
}
