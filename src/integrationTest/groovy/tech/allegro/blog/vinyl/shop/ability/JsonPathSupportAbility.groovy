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

  String apply(String validInputJson, Map<String, Object> jsonPathNewValue) {
    String jsonPath = jsonPathNewValue.entrySet().first().getKey()
    Object newValue = jsonPathNewValue.entrySet().first().getValue()
    return apply(validInputJson, jsonPath, newValue)
  }

  String apply(String originalJson, String jsonPath, Object newValue) {
    JsonPath.using(configuration).parse(originalJson).set(JsonPath.compile(jsonPath), newValue).jsonString()
  }
}
