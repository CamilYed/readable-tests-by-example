package tech.allegro.blog.vinyl.shop.ability.sales

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get

trait SpecialPriceProviderAbility {

    @Autowired
    private WireMockServer wireMockServer

    private static final String SPECIAL_PRICE_SERVICE_URL = "/promotion/FREE_DELIVERY_ORDER_PRICE_ID"

    void minimumOrderValueForFreeDeliveryIs(MoneyJsonBuilder anAmount) {
        def body = JsonOutput.toJson(anAmount.toMap())
        wireMockServer.stubFor(get(SPECIAL_PRICE_SERVICE_URL)
                .willReturn(getPromotionResponse()
                                .withBody(body)
                )
        )
    }

    private getPromotionResponse() {
        return aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
    }
}
