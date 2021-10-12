package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderListingAbility
import tech.allegro.blog.vinyl.shop.ability.order.OrderModificationAbility

import static tech.allegro.blog.vinyl.shop.assertions.OrderListingAssertion.assertThatListing
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anOrder
import static tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.order.OrderItemChangeQuantityJsonBuilder.anItemToChange

class OrderModificationAcceptanceSpec extends BaseIntegrationTest implements
  CreateOrderAbility,
  OrderModificationAbility,
  OrderListingAbility {

  static final String ID = "ORDER_ID_001"
  static final String CLIENT_ID = "CLIENT_ID_001"
  static final String PRODUCT_ID = "PRODUCT_ID_001"

  // @formatter:off
  def "should change the item quantity"() {
    given:
        thereIsUnpaid(
          anOrder()
            .withOrderId(ID)
            .withItem(anItem()
              .withProductId(PRODUCT_ID)
              .withCost(euro(40.00))
              .withQuantity(1)
            )
        )

    when:
        modifyOrderWith(ID, anItemToChange()
          .withProductId(PRODUCT_ID)
          .withQuantityChange(10)
        )

    and:
        def ordersViewResponse = listOrder(ID)

    then:
        assertThatListing(ordersViewResponse)
          .succeeded()
          .hasOrderIdThat(ID)
            .hasClientId(CLIENT_ID)
            .hasItemThat(PRODUCT_ID)
              .withUnitPrice(euro(40.00))
              .withQuantity(10)
  }
  // @formatter:on
}
