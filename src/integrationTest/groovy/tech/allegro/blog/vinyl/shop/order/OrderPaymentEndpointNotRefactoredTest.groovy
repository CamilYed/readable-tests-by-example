package tech.allegro.blog.vinyl.shop.order

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import tech.allegro.blog.vinyl.shop.BaseIntegrationNotRefactoredTest
import tech.allegro.blog.vinyl.shop.catalogue.domain.VinylId
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.common.money.Money
import tech.allegro.blog.vinyl.shop.order.domain.Order
import tech.allegro.blog.vinyl.shop.order.domain.OrderId
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE

class OrderPaymentEndpointNotRefactoredTest extends BaseIntegrationNotRefactoredTest {

  @Autowired
  private OrderRepository orderRepository

  @Autowired
  private TestRestTemplate restTemplate

  @Autowired
  WireMockServer wireMockServer

  static final ClientId CLIENT_ID_1 = ClientId.of("CLIENT_ID_1")
  static final OrderId ORDER_ID_1 = OrderId.of("1")
  static final VinylId PRODUCT_ID_1 = VinylId.of("1")
  static final Money _40_EUR = Money.of(40.00)

  def "shouldn't charge for delivery when the client has a VIP status"() {
    given: "There is a client order with amount 40 EUR"
        def order = new Order(ORDER_ID_1, null, true)
        order.addItem(PRODUCT_ID_1, _40_EUR)
        orderRepository.save(order)

    and: "The client has a VIP reputation"
        wireMockServer.stubFor(
          get("/reputation/${CLIENT_ID_1.value}")
            .withHeader(ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
            .willReturn(aResponse()
              .withBody("""{
                                  "reputation": "VIP",
                                  "clientId": "${CLIENT_ID_1.value}"
                                 }
                              """)
              .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
            )
        )

    when: "When the client pays the order of 40 EUR"
        def body = """{
                                "clientId": "$CLIENT_ID_1.value",
                                "amount":   "${_40_EUR.value.toString()}"
                              }
                          """.toString()
        def requestEntity = buildHttpEntity(body, "application/json",  "application/json")
        def response = restTemplate.exchange(localUrl("/orders/$ORDER_ID_1.value/payment"), HttpMethod.POST, requestEntity, Map)

    then: "The order has been paid correctly"
        response.statusCode == HttpStatus.ACCEPTED

//    and: "The payment system was notified"
//
//    and: "The free track music was sent to the client's mailbox"
  }
}
