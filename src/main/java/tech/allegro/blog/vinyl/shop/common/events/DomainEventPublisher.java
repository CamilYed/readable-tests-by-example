package tech.allegro.blog.vinyl.shop.common.events;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.function.Predicate;

public interface DomainEventPublisher {

  default void publish(Event event) {
    if (event instanceof Event.DomainEvent) {
      publish((Event.DomainEvent) event);
    }
  }

  void publish(Event.DomainEvent event);

  @RequiredArgsConstructor
  class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(Event.DomainEvent event) {
      applicationEventPublisher.publishEvent(event);
    }
  }
}
