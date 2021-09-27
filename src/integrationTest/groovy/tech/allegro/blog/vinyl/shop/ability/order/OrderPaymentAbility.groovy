package tech.allegro.blog.vinyl.shop.ability.order

import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import spock.util.concurrent.PollingConditions
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder
import tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.delivery.domain.Delivery
import tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder
import tech.allegro.blog.vinyl.shop.order.domain.OrderPayFailedEventBuilder

import static groovy.json.JsonOutput.toJson
import static org.mockito.Mockito.times
import static tech.allegro.blog.vinyl.shop.order.domain.OrderPaidEventBuilder.anOrderPaidEvent
import static tech.allegro.blog.vinyl.shop.order.domain.OrderPayFailedEventBuilder.aDifferentAmount

trait OrderPaymentAbility implements MakeRequestAbility {

    @SpyBean
    private DomainEventPublisher domainEventPublisher

    private PollingConditions pollingConditions = new PollingConditions(timeout: 5)

    ResponseEntity<Map> clientMakeThe(PayOrderJsonBuilder aPayment = aPayment()) {
        def jsonBody = toJson(aPayment.toMap())
        return makeRequest(
                url: "/orders/${aPayment.orderId}/payment",
                method: HttpMethod.PUT,
                body: jsonBody,
                contentType: "application/json",
                accept: "application/json",
        )
    }

    void assertThatClientHasNotPaidForDelivery(OrderPaidEventBuilder anEventBuilder
                                                       = anOrderPaidEvent().anOrderPaidEventWithFreeDelivery()) {
        pollingConditions.eventually {
            Mockito.verify(domainEventPublisher, times(1))
                    .publish(anEventBuilder.build())
        }
    }

    void assertThatClientPaidForDeliveryWithAmount(MoneyJsonBuilder anAmount) {
        pollingConditions.eventually {
            Mockito.verify(domainEventPublisher, times(1))
                    .publish(anOrderPaidEvent()
                            .withDelivery(Delivery.standardDelivery(Money.of(anAmount.amount, anAmount.currency)))
                            .build()
                    )
        }
    }

    void assertThatPaymentNotAcceptedBecauseDifferentAmounts() {
        pollingConditions.eventually {
            Mockito.verify(domainEventPublisher, times(1))
                    .publish(aDifferentAmount().build())
        }
    }
}