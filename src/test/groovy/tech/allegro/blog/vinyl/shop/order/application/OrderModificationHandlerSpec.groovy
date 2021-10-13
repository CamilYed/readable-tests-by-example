package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.abilities.ModifyOrderAbility

import static tech.allegro.blog.vinyl.shop.TestData.CZESLAW_NIEMEN_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.TestData.ORDER_ID
import static tech.allegro.blog.vinyl.shop.assertions.OrderDataSnapshotAssertion.assertThat
import static tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder.anItemQuantityChange
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.ItemBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.anUnpaidOrder
import static tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder.euro
import static tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot

class OrderModificationHandlerSpec extends Specification implements ModifyOrderAbility {

  // @formatter:off
  def "should change the item quantity for unpaid order"() {
    given:
        thereIs(anUnpaidOrder()
                .withId(ORDER_ID)
                  .withItem(anItem()
                    .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                    .withUnitPrice(euro(35.00))
                    .withQuantity(10))
        )

    when:
       OrderDataSnapshot afterChange = changeItemQuantity(anItemQuantityChange()
                                                            .withOrderId(ORDER_ID)
                                                            .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                                                            .withQuantityChange(20)
       )

    then:
        assertThat(afterChange)
          .hasOrderId(ORDER_ID)
          .hasItemThat(CZESLAW_NIEMEN_ALBUM_ID)
            .hasQuantity(20)
            .hasPrice(euro(35.00))
  }
  // @formatter:on
}
