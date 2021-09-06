package tech.allegro.blog.vinyl.shop


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@SpringBootTest(classes = [AppRunner],
  properties = "application.environment=integration",
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [WireMockInitializer])
class BaseIntegrationTest extends Specification {


}
