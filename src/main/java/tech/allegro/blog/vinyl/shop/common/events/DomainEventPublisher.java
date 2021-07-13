package tech.allegro.blog.vinyl.shop.common.events;

import tech.allegro.blog.vinyl.shop.order.domain.DomainEvent;

public interface DomainEventPublisher {

  void saveAndPublish(DomainEvent event);
}
