package tech.allegro.blog.vinyl.shop

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
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility

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
        def httpHeaders = new HttpHeaders()
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
