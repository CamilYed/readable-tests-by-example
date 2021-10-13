package tech.allegro.blog.vinyl.shop.assertions

import tech.allegro.blog.vinyl.shop.catalogue.domain.Vinyl
import tech.allegro.blog.vinyl.shop.common.money.MoneyBuilder
import tech.allegro.blog.vinyl.shop.common.volume.Quantity
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderDataSnapshot

import static java.util.Map.*

class OrderDataSnapshotAssertion {
  private OrderDataSnapshot orderDataSnapshot

  private OrderDataSnapshotAssertion(OrderDataSnapshot orderDataSnapshot) {
    this.orderDataSnapshot = orderDataSnapshot
  }

  static OrderDataSnapshotAssertion assertThat(OrderDataSnapshot snapshot) {
    return new OrderDataSnapshotAssertion(snapshot)
  }

  OrderDataSnapshotAssertion hasOrderId(String orderId) {
    assert orderDataSnapshot.orderId().value() == orderId
    return this
  }

  OrderDataSnapshotAssertion hasClientId(String clientId) {
    assert orderDataSnapshot.clientId().value() == clientId
    return this
  }

  ItemAssertion hasItemThat(String productId) {
    Entry<Vinyl, Quantity> item = findItem(productId)
    assert item != null
    return new ItemAssertion(this, item.key, item.value)
  }

  private Entry<Vinyl, Quantity> findItem(String productId) {
    return orderDataSnapshot.items().find { it -> it.key.vinylId().value() == productId }
  }

  static class ItemAssertion {
    OrderDataSnapshotAssertion parent
    Vinyl vinyl
    Quantity quantity

    private ItemAssertion(OrderDataSnapshotAssertion parent, Vinyl vinyl, Quantity quantity) {
      this.vinyl = vinyl
      this.quantity = quantity
      this.parent = parent
    }

    ItemAssertion hasPrice(MoneyBuilder aPrice) {
      assert vinyl.price() == aPrice.build()
      return this
    }

    ItemAssertion hasQuantity(int aQuantity) {
      assert quantity.value() == aQuantity
      return this
    }

    OrderDataSnapshotAssertion and() {
      return parent
    }
  }
}
