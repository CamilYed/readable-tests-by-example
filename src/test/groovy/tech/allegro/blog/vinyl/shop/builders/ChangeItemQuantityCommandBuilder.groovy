package tech.allegro.blog.vinyl.shop.builders

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.common.volume.QuantityChange
import tech.allegro.blog.vinyl.shop.order.domain.Values

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.ChangeItemQuantityCommand
import static tech.allegro.blog.vinyl.shop.order.domain.Values.*

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class ChangeItemQuantityCommandBuilder {
  String orderId = TestData.ORDER_ID
  String productId = TestData.VINYL_CZESLAW_NIEMEN_ID
  int quantityChange = 1

  static ChangeItemQuantityCommandBuilder anItemQuantityChange() {
    return new ChangeItemQuantityCommandBuilder()
  }

  ChangeItemQuantityCommand build() {
    return new ChangeItemQuantityCommand(
      new OrderId(orderId),
      new VinylId(productId),
      new QuantityChange(quantityChange)
    )
  }
}
