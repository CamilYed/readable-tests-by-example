package tech.allegro.blog.vinyl.shop.builders

import groovy.transform.CompileStatic
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.common.money.QuantityBuilder
import tech.allegro.blog.vinyl.shop.common.volume.Quantity

import static tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder.euro

@CompileStatic
@Builder(builderStrategy = SimpleStrategy, prefix = "with")
class CreateOrderWithIdCommandBuilder {
  String clientId
  String orderId
  Map<Vinyl, Quantity> items = [(TestData.VINYL_CZESLAW_NIEMEN): Quantity.ONE]

  CreateOrderWithIdCommandBuilder withAmount(MoneyBuilder amount) {
    items = [(new Vinyl(TestData.VINYL_CZESLAW_NIEMEN_ID, amount.build())): Quantity.ONE]
    return this
  }

  CreateOrderWithIdCommandBuilder withItem(String productId, QuantityBuilder quantity, MoneyBuilder unitPrice = euro(10.00)) {
    items << [(new Vinyl(new VinylId(productId), unitPrice.build())): quantity.build()]
    return this
  }

}
