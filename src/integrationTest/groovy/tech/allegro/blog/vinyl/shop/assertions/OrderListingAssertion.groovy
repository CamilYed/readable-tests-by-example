package tech.allegro.blog.vinyl.shop.assertions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

class OrderListingAssertion {
  private ResponseEntity<Map> response

  private OrderListingAssertion(ResponseEntity<Map> response) {
    this.response = response
  }

  static assertThatListing(ResponseEntity<Map> response) {
    return new OrderListingAssertion(response)
  }

  OrderListingAssertion succeeded() {
    assert response.statusCode == HttpStatus.OK
    return this
  }

  OrderAssertion hasOrderIdThat(String orderId) {
    Map order = findOrder(orderId)
    assert order != null
    return new OrderAssertion(order)
  }

  private Map findOrder(String orderId) {
    Map order = response.body.orders.find { it.orderId == orderId }
    return order
  }

  static class OrderAssertion {
    private Map order

    OrderAssertion(Map order) {
      this.order = order
    }

    OrderAssertion hasClientId(String clientId) {
      assert order.clientId == clientId
      return this
    }

    ItemAssertion hasItemThat(String productId) {
      Map item = findItem(productId)
      assert item != null
      return new ItemAssertion(item, this)
    }

    private Map findItem(String productId) {
      return order.items.find { it.productId == productId }
    }
  }

  static class ItemAssertion {
    private Map item
    private OrderAssertion parentAssertion

    ItemAssertion(Map item, OrderAssertion parentAssertion) {
      this.item = item
      this.parentAssertion = parentAssertion
    }

    ItemAssertion withUnitPrice(MoneyJsonBuilder aCost) {
      assert item.unitPrice == aCost.toMap()
      return this
    }

    ItemAssertion withQuantity(int quantity) {
      assert item.quantity == quantity
      return this
    }

    OrderAssertion and() {
      return parentAssertion
    }
  }
}
