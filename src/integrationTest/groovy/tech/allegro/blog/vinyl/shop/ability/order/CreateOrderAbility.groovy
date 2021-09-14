package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithIdJsonBuilder

import static groovy.json.JsonOutput.toJson

trait CreateOrderAbility implements MakeRequestAbility {

    void thereIs(CreateOrderWithIdJsonBuilder anOrder) {
        def response = upsert(anOrder)
        assert response.statusCode == HttpStatus.CREATED
    }

    ResponseEntity<Map> create(CreateOrderWithIdJsonBuilder anOrder) {
        return upsert(anOrder)
    }

    private ResponseEntity<Map> upsert(CreateOrderWithIdJsonBuilder anOrder) {
        def jsonBody = toJson(anOrder.toMap())
        return makeRequest(
                url: "/orders/${anOrder.orderId}",
                method: HttpMethod.PUT,
                contentType: "application/json",
                body: jsonBody,
                accept: "application/json",
        )
    }
}
