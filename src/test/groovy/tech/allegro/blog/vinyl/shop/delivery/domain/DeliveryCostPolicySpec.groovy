package tech.allegro.blog.vinyl.shop.delivery.domain

import io.github.joke.spockmockable.Mockable
import spock.lang.Specification
import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider

import static tech.allegro.blog.vinyl.shop.TestData.STANDARD
import static tech.allegro.blog.vinyl.shop.TestData.VIP

@Mockable(Money)
class DeliveryCostPolicySpec extends Specification {

  DeliveryCostProvider deliveryCostProvider = GroovyStub()
  SpecialPriceProvider specialPriceProvider = GroovyStub()

  static final Boolean yes = true
  static final Boolean no = false

  @Subject
  DeliveryCostPolicy deliveryCostPolicy = DeliveryCostPolicy.defaultPolicy(deliveryCostProvider, specialPriceProvider)

  def "should calculate delivery cost"() {
    given:
        currentDeliveryCostAccordingToCourierSystem(euro(25.00), courierSystemIsAvailable)

    and:
        assert Delivery.standardDeliveryWithDefaultPrice().cost() == euro(20.00)

    and:
        specialPriceProvider.getMinimumOrderValueForFreeDelivery() >> minimumOrderValueForFreeDelivery

    when:
        Money deliveryCost = deliveryCostPolicy.calculate(orderValue, clientReputation).cost()

    then:
        deliveryCost == expectedDeliveryCost

    where:
        orderValue  || clientReputation || minimumOrderValueForFreeDelivery || courierSystemIsAvailable || expectedDeliveryCost
        euro(39.99) || STANDARD         || euro(40.00)                      || yes                      || euro(25.00)
        euro(39.99) || VIP              || euro(40.00)                      || yes                      || euro(0.0)
        euro(40.00) || STANDARD         || euro(40.00)                      || yes                      || euro(0.0)
        euro(40.00) || VIP              || euro(40.00)                      || yes                      || euro(0.0)
        euro(39.99) || STANDARD         || euro(40.00)                      || no                       || euro(20.00)
        euro(39.99) || VIP              || euro(40.00)                      || no                       || euro(0.0)
        euro(40.00) || STANDARD         || euro(40.00)                      || no                       || euro(0.0)
        euro(40.00) || VIP              || euro(40.00)                      || no                       || euro(0.0)
  }

  private void currentDeliveryCostAccordingToCourierSystem(Money cost, Boolean courierSystemAvailable) {
    if (courierSystemAvailable)
      deliveryCostProvider.currentCost() >> cost
    else
      deliveryCostProvider.currentCost() >>> new RuntimeException()
  }

  private static Money euro(BigDecimal value) {
    return MoneyBuilder.euro(value).build()
  }
}
