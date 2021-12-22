package tech.allegro.blog.vinyl.shop

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import groovy.transform.NamedParam
import groovy.transform.NamedVariant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.ContextConfiguration
import spock.lang.Shared
import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility
import tech.allegro.blog.vinyl.shop.common.time.ClockProvider
import tech.allegro.blog.vinyl.shop.infrastructure.WireMockInitializer

import javax.annotation.PostConstruct
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE

@SpringBootTest(classes = [AppRunner],
  properties = "application.environment=integration",
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockInitializer])
class BaseIntegrationTest extends Specification implements MakeRequestAbility {

  @Value('${local.server.port}')
  private int port

  @Autowired
  private TestRestTemplate restTemplate

  @Autowired
  private WireMockServer wireMockServer

  static final Instant CURRENT_DATE = Instant.parse("2021-11-05T00:00:00.00Z")

  @Shared
  static final Clock TEST_CLOCK = Clock.fixed(CURRENT_DATE, ZoneId.systemDefault())

  @PostConstruct
  void init() {
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
  }

  def setupSpec() {
    ClockProvider.setSystemClock(TEST_CLOCK)
  }

  void setup() {
    WireMock.configureFor(wireMockServer.port())
    WireMock.reset()
  }

  @NamedVariant
  ResponseEntity<Map> makeRequest(
    @NamedParam(required = true) String url,
    @NamedParam(required = true) HttpMethod method,
    @NamedParam(required = false) String contentType = null,
    @NamedParam(required = false) Object body = null,
    @NamedParam(required = false) String accept = null,
    @NamedParam(required = false) Map<String, String> headers) {
    def httpHeaders = buildHeaders(contentType, accept, headers)
    return restTemplate.exchange(localUrl(url), method, new HttpEntity<>(body, httpHeaders), Map)
  }

  private static HttpHeaders buildHeaders(String contentType, String accept, Map<String, String> headers) {
    HttpHeaders httpHeaders = new HttpHeaders()
    !contentType ?: httpHeaders.add(CONTENT_TYPE, contentType)
    !accept ?: httpHeaders.add(ACCEPT, accept)
    (headers ?: [:]).forEach { key, value ->
      httpHeaders.add(key, value)
    }
    return httpHeaders
  }

  private String localUrl(String endpoint) {
    return "http://localhost:$port$endpoint"
  }
}
