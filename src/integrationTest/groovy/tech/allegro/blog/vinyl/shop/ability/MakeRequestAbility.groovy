package tech.allegro.blog.vinyl.shop.ability

import groovy.transform.NamedParam
import groovy.transform.NamedVariant
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity

interface MakeRequestAbility {

    @NamedVariant
    ResponseEntity<Map> makeRequest(
            @NamedParam(required = true) String url,
            @NamedParam(required = true) HttpMethod method,
            @NamedParam(required = false) String contentType,
            @NamedParam(required = false) Object body,
            @NamedParam(required = false) String accept,
            @NamedParam(required = false) Map<String, String> headers)
}
