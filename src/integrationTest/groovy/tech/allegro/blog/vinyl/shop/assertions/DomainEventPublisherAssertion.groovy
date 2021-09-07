package tech.allegro.blog.vinyl.shop.assertions

import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.SpyBean
import spock.util.concurrent.PollingConditions
import tech.allegro.blog.vinyl.shop.builders.order.OrderPaidEventBuilder
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher

import static org.mockito.Mockito.times

trait DomainEventPublisherAssertion {

    @SpyBean
    private DomainEventPublisher domainEventPublisher

    private PollingConditions pollingConditions = new PollingConditions(timeout: 5)

    void assertThatNotificationAboutSuccessfulPaymentWasSentOnce(OrderPaidEventBuilder anEventBuilder) {
        pollingConditions.eventually {
            Mockito.verify(domainEventPublisher, times(1))
                .publish(anEventBuilder.build())
        }
    }
}
