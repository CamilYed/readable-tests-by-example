package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility

import static tech.allegro.blog.vinyl.shop.assertions.OrderCreationAssertion.assertThatOrder
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anUnpaidOrder
import static tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder.anItem

class OrderCreationAcceptanceSpec extends BaseIntegrationTest implements CreateOrderAbility {


  // @formatter:off
  def "should create unpaid order when not exists"() {
    when:
        def creationResult = upsert(anUnpaidOrder()
                                        .withOrderId(TestData.ORDER_ID)
                                        .withClientId(TestData.CLIENT_ID)
                                        .withItem(anItem()
                                                      .withProductId(TestData.CZESLAW_NIEMEN_ALBUM_ID)
                                                      .withUnitPrice(euro(40.00))
                                                      .withQuantity(1))
        )

    then:
        assertThatOrder(creationResult)
          .succeeded()
            .hasOrderId(TestData.ORDER_ID)
            .hasClientId(TestData.CLIENT_ID)
            .hasItemThat()
              .hasProductId( TestData.CZESLAW_NIEMEN_ALBUM_ID)
              .withCost(euro(40.00))
              .withQuantity(1)
  }
  // @formatter:on
}
