package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.OrderItemChangeQuantityJsonBuilder

import static groovy.json.JsonOutput.toJson

trait OrderModificationAbility implements MakeRequestAbility {

  ResponseEntity<Map> modifyOrderWith(String orderId, OrderItemChangeQuantityJsonBuilder anItemToChange) {
    def jsonBody = toJson(anItemToChange.toMap())
    return makeRequest(
      url: "/orders/$orderId/items/${anItemToChange.productId}",
      method: HttpMethod.PATCH,
      contentType: "application/json",
      body: jsonBody,
      accept: "application/json",
    )
  }
}
