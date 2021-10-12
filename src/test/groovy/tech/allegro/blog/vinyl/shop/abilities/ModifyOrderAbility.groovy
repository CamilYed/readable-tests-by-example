package tech.allegro.blog.vinyl.shop.abilities

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder
import tech.allegro.blog.vinyl.shop.common.result.Result
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory

trait ModifyOrderAbility implements AddOrderAbility {

  @Subject
  OrderModificationHandler orderModificationHandler

  def setup() {
    orderModificationHandler = new OrderModificationHandler(orderRepository, new OrderFactory())
  }

  Result<Void> changeItemQuantity(ChangeItemQuantityCommandBuilder anItemQuantityChange) {
    return orderModificationHandler.handle(anItemQuantityChange.build())
  }
}
