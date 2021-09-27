package tech.allegro.blog.vinyl.shop.common.events

trait DomainEventPublisherAbility {

    final DomainEventPublisher domainEventPublisher = new InMemoryDomainEventPublisher()

    void assertThatEventWasPublishedOnce(Event.DomainEvent domainEvent) {
        assert domainEventPublisher.count() == 1
        assert domainEventPublisher.contains(domainEvent)
    }
}