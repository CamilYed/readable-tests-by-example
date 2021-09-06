package tech.allegro.blog.vinyl.shop.order.domain

import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money

class SampleOrder {

  static Order build(ClientId clientId, OrderId orderId, Money price, VinylId productId, Boolean unpaid = true) {
    def orderLines = Order.OrderLines.empty()
    orderLines.add(productId, price)
    return new Order(orderId, clientId, orderLines, null, unpaid)
  }
}
