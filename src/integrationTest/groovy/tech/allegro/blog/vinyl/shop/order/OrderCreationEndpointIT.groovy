package tech.allegro.blog.vinyl.shop.order


import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.JsonPathSupportAbility
import tech.allegro.blog.vinyl.shop.ability.MakeRequestAbility

class OrderCreationEndpointIT extends BaseIntegrationTest implements MakeRequestAbility, JsonPathSupportAbility {

  final static String validInputJson = """{
                                              "clientId":"CLIENT_ID_001",
                                              "items":[
                                                 {
                                                    "itemUnitPrice":{
                                                       "productId":"PRODUCT_ID_001",
                                                       "price":{
                                                          "amount":40.00,
                                                          "currency":"EUR"
                                                       }
                                                    },
                                                    "quantity":1
                                                 }
                                              ]
                                          }
                                       """


  def "should validate the input JSON object representing the client's order: #testCaseDescription"() {
    when:
        ResponseEntity<Map> response = makeRequest(
          url: "/orders",
          method: HttpMethod.POST,
          contentType: "application/json",
          body: modifyInputJsonWith(validInputJson, pathToChange),
          accept: "application/json",
        )

    then:
        response.statusCode == HttpStatus.BAD_REQUEST
        response.body.errors.size() == 1

    and:
        with(response.body.errors[0]) {
          assert it == [
            "code"       : "400 BAD_REQUEST",
            "message"    : expectedErrorMessage,
            "path"       : expectedPath,
            "userMessage": "Validation error.",
            "details"    : null
          ]
        }

    where:
        pathToChange                                      || expectedErrorMessage                 || expectedPath                              || testCaseDescription
        ['$.clientId': null]                              || "must not be blank"                  || '$.clientId'                              || "The clientId is null"
        ['$.clientId': "  "]                              || "must not be blank"                  || '$.clientId'                              || "The clientId is blank"
        ['$.items': null]                                 || "must not be empty"                  || '$.items'                                 || "The items list is null"
        ['$.items': []]                                   || "must not be empty"                  || '$.items'                                 || "The items list is empty"
        ['$.items[0].quantity': 0]                        || "must be greater than or equal to 1" || '$.items[0].quantity'                     || "The item quantity is less than 1"
        ['$.items[0].itemUnitPrice': null]                || "must not be null"                   || '$.items[0].itemUnitPrice'                || "The item unit price is null"
        ['$.items[0].itemUnitPrice.productId': null]      || "must not be blank"                  || '$.items[0].itemUnitPrice.productId'      || "The item product id is null"
        ['$.items[0].itemUnitPrice.productId': "  "]      || "must not be blank"                  || '$.items[0].itemUnitPrice.productId'      || "The item product id is blank"
        ['$.items[0].itemUnitPrice.price': null]          || "must not be null"                   || '$.items[0].itemUnitPrice.price'          || "The item price is null"
        ['$.items[0].itemUnitPrice.price.amount': null]   || "must not be blank"                  || '$.items[0].itemUnitPrice.price.amount'   || "The item price amount is null"
        ['$.items[0].itemUnitPrice.price.amount': "  "]   || "must not be blank"                  || '$.items[0].itemUnitPrice.price.amount'   || "The item price amount is blank"
        ['$.items[0].itemUnitPrice.price.currency': null] || "must not be blank"                  || '$.items[0].itemUnitPrice.price.currency' || "The item price currency is null"
        ['$.items[0].itemUnitPrice.price.currency': "  "] || "must not be blank"                  || '$.items[0].itemUnitPrice.price.currency' || "The item price currency is blank"
  }

  private String modifyInputJsonWith(String validInputJson, Map<String, Object> jsonPathNeValue) {
    String jsonPath = jsonPathNeValue.entrySet().first().getKey()
    Object newValue = jsonPathNeValue.entrySet().first().getValue()
    return apply(validInputJson, jsonPath, newValue)
  }
}
