package tech.allegro.blog.vinyl.shop.ability

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider

trait JsonPathSupportAbility {

  private static Configuration configuration = Configuration.builder()
    .jsonProvider(new JacksonJsonNodeJsonProvider())
    .mappingProvider(new JacksonMappingProvider())
    .build()

  String apply(String originalJson, String jsonPath, Object newValue) {
    JsonPath.using(configuration).parse(originalJson).set(JsonPath.compile(jsonPath), newValue).jsonString()
  }
}
