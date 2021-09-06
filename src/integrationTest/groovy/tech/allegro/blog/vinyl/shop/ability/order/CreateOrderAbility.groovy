package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.CreateOrderWithItemsJsonBuilder

import static groovy.json.JsonOutput.toJson

trait CreateOrderAbility implements MakeRequestAbility {

    void thereIs(CreateOrderWithItemsJsonBuilder anOrder) {
        def jsonBody = toJson(anOrder.toMap())
        def response = makeRequest(
            url: "/orders/${anOrder.orderId}",
            method: HttpMethod.PUT,
            contentType: "application/json",
            body: jsonBody,
            accept: "application/json",
        )
        assert response.statusCode == HttpStatus.CREATED
    }
}
