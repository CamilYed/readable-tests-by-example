package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder

import static tech.allegro.blog.vinyl.shop.TestData.CLIENT_ID
import static tech.allegro.blog.vinyl.shop.TestData.CZESLAW_NIEMEN_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.TestData.ORDER_ID
import static tech.allegro.blog.vinyl.shop.assertions.OrderCreationAssertion.assertThatOrder
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anUnpaidOrder
import static tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder.anItem

class OrderCreationAcceptanceSpec extends BaseIntegrationTest implements CreateOrderAbility {


  // @formatter:off
  def "should create unpaid order with given ID when not exists"() {
    given:
      CreateOrderJsonBuilder anUnpaidOrder = anUnpaidOrder()
                                                .withClientId(CLIENT_ID)
                                                .withItem(anItem()
                                                    .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                                                    .withUnitPrice(euro(40.00))
                                                    .withQuantity(1))
    when:
        def creationResult = createWithGivenId(anOrder:  anUnpaidOrder, orderId:  ORDER_ID)

    then:
        assertThatOrder(creationResult)
          .succeeded()
            .hasOrderId(ORDER_ID)
            .hasClientId(CLIENT_ID)
            .hasItemThat()
              .hasProductId( CZESLAW_NIEMEN_ALBUM_ID)
              .withCost(euro(40.00))
              .withQuantity(1)
  }

  def "should create unpaid order without ID when not exists"() {
    given:
        CreateOrderJsonBuilder anUnpaidOrder = anUnpaidOrder()
          .withClientId(CLIENT_ID)
          .withItem(anItem()
            .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
            .withUnitPrice(euro(40.00))
            .withQuantity(1))
    when:
        def creationResult = create(anUnpaidOrder)

    then:
        assertThatOrder(creationResult)
          .succeeded()
          .hasAssignedOrderId()
          .hasClientId(CLIENT_ID)
          .hasItemThat()
            .hasProductId( CZESLAW_NIEMEN_ALBUM_ID)
            .withCost(euro(40.00))
            .withQuantity(1)
  }
  // @formatter:on
}
