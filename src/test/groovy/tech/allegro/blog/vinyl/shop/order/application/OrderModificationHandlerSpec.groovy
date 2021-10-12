package tech.allegro.blog.vinyl.shop.order.application

import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.abilities.ModifyOrderAbility

import static tech.allegro.blog.vinyl.shop.TestData.CZESLAW_NIEMEN_ALBUM_ID
import static tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder.anItemQuantityChange
import static tech.allegro.blog.vinyl.shop.common.money.QuantityBuilder.quantity
import static tech.allegro.blog.vinyl.shop.builders.OrderDataSnapshotBuilder.anUnpaidOrder

class OrderModificationHandlerSpec extends Specification implements ModifyOrderAbility {

  def "should change item quantity for unpaid order"() {
    given:
        thereIs(anUnpaidOrder().withItem(CZESLAW_NIEMEN_ALBUM_ID, quantity(10)))

    when:
        def result = changeItemQuantity(anItemQuantityChange()
                                          .withProductId(CZESLAW_NIEMEN_ALBUM_ID)
                                          .withQuantityChange(20)
        )

    then:
        result.isSuccess()
  }
}
