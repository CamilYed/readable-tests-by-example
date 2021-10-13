package tech.allegro.blog.vinyl.shop.abilities

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder
import tech.allegro.blog.vinyl.shop.common.result.Result
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory
import tech.allegro.blog.vinyl.shop.order.domain.Values

trait ModifyOrderAbility implements AddOrderAbility {

  @Subject
  OrderModificationHandler orderModificationHandler

  def setup() {
    orderModificationHandler = new OrderModificationHandler(orderRepository, new OrderFactory())
  }

  Values.OrderDataSnapshot changeItemQuantity(ChangeItemQuantityCommandBuilder anItemQuantityChange) {
    OrderModificationHandler.ChangeItemQuantityCommand command = anItemQuantityChange.build()
    Result<Void> result = orderModificationHandler.handle(command)
    assert result.isSuccess()
    return orderRepository.findBy(command.orderId()).get()
  }
}
