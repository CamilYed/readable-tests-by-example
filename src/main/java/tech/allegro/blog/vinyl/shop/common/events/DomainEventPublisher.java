package tech.allegro.blog.vinyl.shop.common.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

public interface DomainEventPublisher {

  void publish(DomainEvent event);

  @RequiredArgsConstructor
  class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

      public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
          this.applicationEventPublisher = applicationEventPublisher;
      }

      @Override
    public void publish(DomainEvent event) {
      applicationEventPublisher.publishEvent(event);
    }
  }
}
