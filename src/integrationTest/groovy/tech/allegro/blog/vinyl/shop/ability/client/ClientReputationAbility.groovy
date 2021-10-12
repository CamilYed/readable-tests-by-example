package tech.allegro.blog.vinyl.shop.ability.client


import org.springframework.http.HttpHeaders
import tech.allegro.blog.vinyl.shop.TestData

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor

trait ClientReputationAbility {

  private static final String CLIENT_REPUTATION_SERVICE_URL = "/reputation"

  def clientIsVip(String clientId = TestData.CLIENT_ID) {
    stubFor(get(buildUrl(clientId))
      .willReturn(
        getReputationResponse()
          .withBody(
            """
                                {
                                    "clientId": "${clientId}",
                                    "reputation": "VIP"
                                }
                              """
          )
      ))
  }

  def clientIsNotVip(String clientId = TestData.CLIENT_ID) {
    stubFor(get(buildUrl(clientId))
      .willReturn(
        getReputationResponse()
          .withBody(
            """
                                {
                                    "clientId": "${clientId}",
                                    "reputation": "STANDARD"
                                }
                              """
          )
      ))
  }

  private getReputationResponse() {
    return aResponse()
      .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
  }

  private static String buildUrl(String clientId) {
    return CLIENT_REPUTATION_SERVICE_URL + "/${clientId}"
  }
}
