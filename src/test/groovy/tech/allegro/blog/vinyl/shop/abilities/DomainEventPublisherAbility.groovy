package tech.allegro.blog.vinyl.shop.abilities

import tech.allegro.blog.vinyl.shop.common.events.DomainEvent
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.common.events.InMemoryDomainEventPublisher

trait DomainEventPublisherAbility {

  final DomainEventPublisher domainEventPublisher = new InMemoryDomainEventPublisher()

  void assertThatEventWasPublishedOnce(DomainEvent domainEvent) {
    assert domainEventPublisher.count() == 1
    assert domainEventPublisher.contains(domainEvent)
  }

  void assertThatAnyEventWasNotPublished() {
    assert domainEventPublisher.count() == 0
  }
}
