package tech.allegro.blog.vinyl.shop.ability.delivery

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get

trait CourierSystemAbility {

    @Autowired
    private WireMockServer wireMockServer

    private static final String DELIVERY_COST_SERVICE_URL = "/current-cost"

    void currentDeliveryCostIs(MoneyJsonBuilder aCost) {
        def body = JsonOutput.toJson(aCost.toMap())
        wireMockServer.stubFor(get(DELIVERY_COST_SERVICE_URL)
                .willReturn(getCurrentCostResponse()
                        .withBody(body)
                )
        )
    }

    void externalCourierSystemIsUnavailable() {
        wireMockServer.stubFor(get(DELIVERY_COST_SERVICE_URL)
                .willReturn(aResponse().withFixedDelay(5000))
        )
    }

    private getCurrentCostResponse() {
        return aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
    }
}
