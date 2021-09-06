package tech.allegro.blog.vinyl.shop.ability.order

import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.builders.order.PayOrderJsonBuilder

import static groovy.json.JsonOutput.toJson

trait OrderPaymentAbility implements MakeRequestAbility {

    ResponseEntity<Map> makeThe(PayOrderJsonBuilder aPayment) {
        def jsonBody = toJson(aPayment.toMap())
        return makeRequest(
            url: "/orders/${aPayment.orderId}/payment",
            method: HttpMethod.PUT,
            body: jsonBody,
            contentType: "application/json",
            accept: "application/json",
        )
    }
}