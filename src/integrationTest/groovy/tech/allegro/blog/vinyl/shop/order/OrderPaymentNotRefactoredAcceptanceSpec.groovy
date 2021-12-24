package tech.allegro.blog.vinyl.shop.order

import com.github.tomakehurst.wiremock.WireMockServer
import org.spockframework.spring.SpringSpy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import spock.util.concurrent.PollingConditions
import tech.allegro.blog.vinyl.shop.BaseIntegrationNotRefactoredTest
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.events.DomainEventPublisher
import tech.allegro.blog.vinyl.shop.order.domain.Events

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE
import static org.springframework.http.HttpMethod.PUT
import static org.springframework.http.MediaType.APPLICATION_JSON

class OrderPaymentNotRefactoredAcceptanceSpec extends BaseIntegrationNotRefactoredTest {

  @Autowired
  private TestRestTemplate restTemplate

  @SpringSpy
  private DomainEventPublisher domainEventPublisher

  @SpringSpy
  private FreeMusicTrackSender freeMusicTrackSender

  @Autowired
  WireMockServer wireMockServer

  private PollingConditions pollingConditions = new PollingConditions(timeout: 5)

  static final String ORDER_ID_1 = "ORDER_ID_1"
  static final String CLIENT_ID_1 = "CLIENT_ID_1"
  static final String PRODUCT_ID_1 = "PRODUCT_ID_001"

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given:
        def body = """
          {
             "clientId":"${CLIENT_ID_1}",
             "items":[
                {
                   "itemUnitPrice":{
                      "productId":"${PRODUCT_ID_1}",
                      "price":{
                         "amount":"40.00",
                         "currency":"EUR"
                      }
                   },
                   "quantity":1
                }
             ]
          }
        """.toString()
        def requestEntity = buildHttpEntity(body, APPLICATION_JSON.toString(), APPLICATION_JSON.toString())
        def response = restTemplate.exchange(localUrl("/orders/$ORDER_ID_1"), PUT, requestEntity, Map)
    and:
        assert response.statusCode == HttpStatus.CREATED

    and:
        wireMockServer.stubFor(
          get("/reputation/${CLIENT_ID_1}")
            .withHeader(ACCEPT, equalTo(APPLICATION_JSON.toString()))
            .willReturn(aResponse()
              .withBody(
                """{
                            "reputation": "VIP",
                            "clientId": "${CLIENT_ID_1}"
                         }""")
              .withHeader(CONTENT_TYPE, APPLICATION_JSON.toString())
            )
        )
    when:
        body = """
          {
             "clientId": "${CLIENT_ID_1}",
             "cost": { "amount": "40.00", "currency": "EUR" }
           }
          """.toString()
        requestEntity = buildHttpEntity(body, APPLICATION_JSON.toString(), APPLICATION_JSON.toString())
        response = restTemplate.exchange(localUrl("/orders/$ORDER_ID_1/payment"), PUT, requestEntity, Map)

    then:
        response.statusCode == HttpStatus.ACCEPTED

    and:
        1 * domainEventPublisher.publish(_ as Events.OrderPaid)

    and:
        pollingConditions.eventually {
          1 * freeMusicTrackSender.send(new ClientId(CLIENT_ID_1))
        }
  }
}
