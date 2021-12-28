package tech.allegro.blog.vinyl.shop.ability.order

import groovy.transform.NamedParam
import groovy.transform.NamedParams
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder

import static groovy.json.JsonOutput.toJson

trait CreateOrderAbility implements MakeRequestAbility {

  void thereIs(CreateOrderJsonBuilder anUnpaidOrder, String orderId = TestData.ORDER_ID) {
    def response = createWithGivenId(anOrder: anUnpaidOrder, orderId: orderId)
    assert response.statusCode == HttpStatus.CREATED
  }

  ResponseEntity<Map> create(CreateOrderJsonBuilder anOrder) {
    def jsonBody = toJson(anOrder.toMap())
    return makeRequest(
      url: "/orders",
      method: HttpMethod.POST,
      contentType: "application/json",
      body: jsonBody,
      accept: "application/json",
    )
  }

  ResponseEntity<Map> createWithGivenId(
    @NamedParams([
      @NamedParam(required = true, type = CreateOrderJsonBuilder),
      @NamedParam(required = true, type = String)
    ]) Map<String, Object> params) {
    def jsonBody = toJson(params.anOrder.toMap())
    return makeRequest(
      url: "/orders/${params.orderId}",
      method: HttpMethod.PUT,
      contentType: "application/json",
      body: jsonBody,
      accept: "application/json",
    )
  }
}
