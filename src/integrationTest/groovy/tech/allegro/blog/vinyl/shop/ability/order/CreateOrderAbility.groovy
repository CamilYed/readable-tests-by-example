package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder

import static groovy.json.JsonOutput.toJson

trait CreateOrderAbility implements MakeRequestAbility {

  void thereIs(CreateOrderJsonBuilder anUnpaidOrder, String orderId = TestData.ORDER_ID) {
    def response = upsert(anUnpaidOrder, orderId)
    assert response.statusCode == HttpStatus.CREATED
  }

  ResponseEntity<Map> create(CreateOrderJsonBuilder anOrder) {
    return upsert(anOrder, anOrder.orderId)
  }

  private ResponseEntity<Map> upsert(CreateOrderJsonBuilder anOrder, String orderId) {
    def jsonBody = toJson(anOrder.toMap())
    return makeRequest(
      url: "/orders/$orderId",
      method: HttpMethod.PUT,
      contentType: "application/json",
      body: jsonBody,
      accept: "application/json",
    )
  }
}
