package tech.allegro.blog.vinyl.shop

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.catalogue.TestFakeFreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender

import static org.springframework.http.HttpHeaders.ACCEPT
import static org.springframework.http.HttpHeaders.CONTENT_TYPE

@SpringBootTest(classes = [AppRunner, TestConfig],
  properties = "application.environment=integration",
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockInitializer])
abstract class BaseIntegrationNotRefactoredTest extends Specification {

  @Value('${local.server.port}')
  protected int port

  protected String localUrl(String endpoint) {
    return "http://localhost:$port$endpoint"
  }

  protected static <T> HttpEntity<T> buildHttpEntity(T body, String contentType, String accept, Map<String, String> additionalHeaders = null) {
    def headers = buildHeaders(contentType, accept, additionalHeaders)
    return new HttpEntity<T>(body, headers)
  }

  protected static HttpHeaders buildHeaders(String contentType, String accept, Map<String, String> headers) {
    def httpHeaders = new HttpHeaders()
    !contentType ?: httpHeaders.add(CONTENT_TYPE, contentType)
    !accept ?: httpHeaders.add(ACCEPT, accept)
    (headers ?: [:]).forEach { key, value ->
      httpHeaders.add(key, value)
    }
    return httpHeaders
  }

  @TestConfiguration
  static class TestConfig {

    @Bean
    @Primary
    FreeMusicTrackSender freeMusicTrackSender() {
      return new TestFakeFreeMusicTrackSender()
    }
  }
}
