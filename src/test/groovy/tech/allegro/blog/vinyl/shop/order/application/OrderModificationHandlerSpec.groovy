package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.abilities.ModifyOrderAbility

import static tech.allegro.blog.vinyl.shop.TestData.CZESLAW_NIEMEN_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.TestData.BOHEMIAN_RHAPSODY_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.TestData.ORDER_ID
import static tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder.anItemQuantityChange
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.ItemBuilder.anItem
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.aPaidOrder
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.anUnpaidOrder
import static tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder.euro

class OrderModificationHandlerSpec extends Specification implements ModifyOrderAbility {

  // @formatter:off
  def "should not modify paid order"() {
    given:
        thereIs(aPaidOrder())

    when:
        def changeResult = changeItemQuantity(anItemQuantityChange()
                                                .withOrderId(ORDER_ID)
                                                .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                                                .withQuantityChange(20)
        )

    then:
        assertThatChangeFailedDueToOrderAlreadyIsPaid(changeResult)
  }


  def "should change the item quantity for unpaid order"() {
    given:
        thereIs(anUnpaidOrder()
                .withId(ORDER_ID)
                  .withItems(
                    anItem()
                      .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                      .withUnitPrice(euro(35.00))
                      .withQuantity(10),
                    anItem()
                      .withProductId(BOHEMIAN_RHAPSODY_ALBUM_ID)
                      .withUnitPrice(euro(55.00))
                      .withQuantity(1)
                  )
        )

    when:
       changeItemQuantity(anItemQuantityChange()
                            .withOrderId(ORDER_ID)
                            .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                            .withQuantityChange(20)
       )

    then:
        assertThatThereIsOrderWithId(ORDER_ID)
          .hasItemWithIdThat(CZESLAW_NIEMEN_ALBUM_ID)
              .hasUnitPrice(euro(35.00))
              .hasQuantity(20)
          .and()
          .hasItemWithIdThat(BOHEMIAN_RHAPSODY_ALBUM_ID)
              .hasUnitPrice(euro(55.00))
              .hasQuantity(1)
  }
  // @formatter:on
}
