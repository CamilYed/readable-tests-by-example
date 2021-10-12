package tech.allegro.blog.vinyl.shop.common.events

class InMemoryDomainEventPublisher implements DomainEventPublisher {

  private final List<DomainEvent> published = []

  @Override
  void publish(DomainEvent event) {
    published.add(event)
  }

  int count() {
    return published.size()
  }

  boolean contains(DomainEvent domainEvent) {
    return published.contains(domainEvent)
  }
}
