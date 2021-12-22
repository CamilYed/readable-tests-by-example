package tech.allegro.blog.vinyl.shop.abilities

import spock.lang.Subject
import tech.allegro.blog.vinyl.shop.builders.ChangeItemQuantityCommandBuilder
import tech.allegro.blog.vinyl.shop.common.result.Result
import tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler
import tech.allegro.blog.vinyl.shop.order.domain.OrderFactory

import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.CanNotModifyPaidOrder
import static tech.allegro.blog.vinyl.shop.order.application.OrderModificationHandler.ChangeItemQuantityCommand

trait ModifyOrderAbility implements OrderAbility {

  @Subject
  OrderModificationHandler orderModificationHandler

  def setup() {
    orderModificationHandler = new OrderModificationHandler(orderRepository, new OrderFactory())
  }

  Result<Void> changeItemQuantity(ChangeItemQuantityCommandBuilder anItemQuantityChange) {
    ChangeItemQuantityCommand command = anItemQuantityChange.build()
    return orderModificationHandler.handle(command)
  }

  void assertThatChangeFailedDueToOrderAlreadyIsPaid(Result<Void> changeResult) {
    assert changeResult.isError() && changeResult.error().cause() instanceof CanNotModifyPaidOrder
  }
}
