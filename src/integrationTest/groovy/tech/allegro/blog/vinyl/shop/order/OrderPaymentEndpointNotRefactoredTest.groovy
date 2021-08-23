package tech.allegro.blog.vinyl.shop.order

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor

class OrderPaymentEndpointNotRefactoredTest extends BaseIntegrationNotRefactoredTest {

  @Autowired
  private OrderRepository orderRepository

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

    and:
        stubGetClientReputationAsVip(CLIENT_ID_1)

    when: "When the client pays the order of 40 EUR"
        def response = pay(ORDER_ID_1, _40_EUR)

    then: "The order has been paid correctly"
        response.statusCode == HttpStatus.ACCEPTED

//    and: "The payment system was notified"
//
//    and: "The free track music was sent to the client's mailbox"
  }

  private void stubGetClientReputationAsVip(ClientId clientId) {
    wireMockServer.stubFor(
      get("/reputation/${clientId.value}")
        .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON.toString()))
        .willReturn(aResponse()
          .withBody("""{
                                "reputation": "VIP",
                                "clientId": "${clientId.value}"
                              }
                          """)
          .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
        )
    )
  }

  private ResponseEntity<Map> pay(OrderId orderId,
                                  Money amount,
                                  String clientId = CLIENT_ID_1.getValue()) {
    def payload = """{
                                "clientId": "$clientId",
                                "amount":   "${amount.value.toString()}"
                              }
                          """
    return makeRequest(
      url: "/payments/orders/$orderId.value",
      method: HttpMethod.POST,
      contentType: "application/json",
      body: payload.toString()
    )
  }
}
