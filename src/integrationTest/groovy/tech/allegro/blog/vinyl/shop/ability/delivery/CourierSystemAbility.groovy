package tech.allegro.blog.vinyl.shop.ability.delivery


import groovy.json.JsonOutput
import org.springframework.http.HttpHeaders
import tech.allegro.blog.vinyl.shop.builders.money.MoneyJsonBuilder

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor

trait CourierSystemAbility {

  private static final String DELIVERY_COST_SERVICE_URL = "/current-cost"

  void currentDeliveryCostIs(MoneyJsonBuilder aCost) {
    def body = JsonOutput.toJson(aCost.toMap())
    stubFor(get(DELIVERY_COST_SERVICE_URL)
      .willReturn(getCurrentCostResponse()
        .withBody(body)
      )
    )
  }

  void externalCourierSystemIsUnavailable() {
    stubFor(get(DELIVERY_COST_SERVICE_URL)
      .willReturn(aResponse().withFixedDelay(5000))
    )
  }

  private getCurrentCostResponse() {
    return aResponse()
      .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
  }
}
