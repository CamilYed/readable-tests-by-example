package tech.allegro.blog.vinyl.shop.common.events

class InMemoryDomainEventPublisher implements DomainEventPublisher {

    private final List<Event.DomainEvent> published = []

    @Override
    void publish(Event.DomainEvent event) {
        published.add(event)
    }

    int count() {
        return published.size()
    }

    boolean contains(Event.DomainEvent domainEvent) {
        return published.contains(domainEvent)
    }
}
