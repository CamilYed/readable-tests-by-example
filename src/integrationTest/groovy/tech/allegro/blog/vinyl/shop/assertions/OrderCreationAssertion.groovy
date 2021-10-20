package tech.allegro.blog.vinyl.shop.assertions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

class OrderCreationAssertion {
  private ResponseEntity<Map> creationResult

  private OrderCreationAssertion(ResponseEntity<Map> response) {
    this.creationResult = response
  }

  static assertThatOrder(ResponseEntity<Map> response) {
    return new OrderCreationAssertion(response)
  }

  OrderCreationAssertion succeeded() {
    assert creationResult.statusCode == HttpStatus.CREATED
    return this
  }

  OrderCreationAssertion hasOrderId(String orderId) {
    assert creationResult.body.orderId == orderId
    return this
  }

  OrderCreationAssertion hasClientId(String clientId) {
    assert creationResult.body.clientId == clientId
    return this
  }

  ItemAssertion hasItemThat() {
    return new ItemAssertion(creationResult.body.items as List<Map>, this)
  }

  static class ItemAssertion {
    private List<Map> items
    private String currentItem
    private OrderCreationAssertion parentAssertion

    ItemAssertion(List<Map> items, OrderCreationAssertion parentAssertion) {
      this.items = items
      this.parentAssertion = parentAssertion
    }

    ItemAssertion hasProductId(String productId) {
      assert items.any { it.itemUnitPrice.productId == productId }
      currentItem = productId
      return this
    }

    ItemAssertion withCost(MoneyJsonBuilder aCost) {
      Map item = items.find { it.itemUnitPrice.productId == currentItem }.itemUnitPrice
      assert item.price == aCost.toMap()
      return this
    }

    ItemAssertion withQuantity(int quantity) {
      def item = items.find { it.itemUnitPrice.productId == currentItem }
      assert item.quantity.value == quantity
      return this
    }

    OrderCreationAssertion and() {
      return parentAssertion
    }
  }
}
