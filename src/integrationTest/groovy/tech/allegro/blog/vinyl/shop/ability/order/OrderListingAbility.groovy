package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility

trait OrderListingAbility implements MakeRequestAbility {

  ResponseEntity<Map> listOrderBy(String orderId = TestData.ORDER_ID) {
    return makeRequest(
      url: "/orders-view/$orderId",
      method: HttpMethod.GET,
      contentType: "application/json",
      accept: "application/json",
    )
  }
}
