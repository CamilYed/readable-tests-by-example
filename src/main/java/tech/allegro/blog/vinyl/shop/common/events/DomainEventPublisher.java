package tech.allegro.blog.vinyl.shop.common.events;

import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent;

public interface DomainEventPublisher {

  void publish(DomainEvent event);

  default void publish(Iterable<DomainEvent> events) {
    events.forEach(this::publish);
  }
}
