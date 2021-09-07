package tech.allegro.blog.vinyl.shop.order

import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility

import static tech.allegro.blog.vinyl.shop.assertions.OrderCreationAssertion.assertThatOrder
import static tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder.euro
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.ItemJsonBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder.anOrder

class OrderCreatorEndpointIT extends BaseIntegrationTest implements CreateOrderAbility {

    static final String ID = "ORDER_ID_001"
    static final String CLIENT_ID = "CLIENT_ID_001"
    static final String PRODUCT_ID = "PRODUCT_ID_001"

    def "should create unpaid order when not exists"() {
        when:
            def creationResult = create(
                    anOrder()
                      .withOrderId(ID)
                      .withClientId(CLIENT_ID)
                      .withItem(
                              anItem()
                                 .withProductId(PRODUCT_ID)
                                 .withCost(euro("40.00"))
                      )
            )

        then:
            assertThatOrder(creationResult).succeeded()
    }
}