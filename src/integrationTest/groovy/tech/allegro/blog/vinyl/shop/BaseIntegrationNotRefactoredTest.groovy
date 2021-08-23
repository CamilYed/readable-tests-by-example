package tech.allegro.blog.vinyl.shop

import com.github.tomakehurst.wiremock.WireMockServer
import groovy.transform.NamedParam
import groovy.transform.NamedVariant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.event.ContextClosedEvent
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Specification

import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE

@SpringBootTest(classes = [AppRunner],
  properties = "application.environment=integration",
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockInitializer])
abstract class BaseIntegrationNotRefactoredTest extends Specification {

  @Autowired
  private TestRestTemplate restTemplate

  @Value('${local.server.port}')
  protected int port

  public static final int WIRE_MOCK_PORT = nextAvailablePort()

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

  private String localUrl(String endpoint) {
    return "http://localhost:$port$endpoint"
  }

  private static HttpHeaders buildHeaders(String contentType, String accept, Map<String, String> headers) {
    def httpHeaders = new HttpHeaders()
    !contentType ?: httpHeaders.add(CONTENT_TYPE, contentType)
    !accept ?: httpHeaders.add(ACCEPT, accept)
    (headers ?: [:]).forEach { key, value ->
      httpHeaders.add(key, value)
    }
    return httpHeaders
  }

  private static class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      WireMockServer wireMockServer = new WireMockServer(WIRE_MOCK_PORT)
      wireMockServer.start()
      configurableApplicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer)

      configurableApplicationContext.addApplicationListener(applicationEvent -> {
        if (applicationEvent instanceof ContextClosedEvent) {
          wireMockServer.stop();
        }
      })
    }
  }

  @DynamicPropertySource
  static void dynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("wiremock.server.port", { WIRE_MOCK_PORT })
  }

  private static int nextAvailablePort() {
    try {
      try (ServerSocket socket = new ServerSocket(0)) {
        return socket.getLocalPort();
      }
    } catch (IOException exception) {
      throw new RuntimeException(exception)
    }
  }
}
