package tech.allegro.blog.vinyl.shop.order

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import tech.allegro.blog.vinyl.shop.BaseIntegrationTest
import tech.allegro.blog.vinyl.shop.ability.order.CreateOrderAbility
import tech.allegro.blog.vinyl.shop.builders.order.ItemJsonBuilder

import static tech.allegro.blog.vinyl.shop.builders.order.CreateOrderJsonBuilder.anUnpaidOrder

class OrderCreationEndpointIT extends BaseIntegrationTest implements CreateOrderAbility {

  def "should validate the input JSON object representing the client's order: #description"() {
    when:
        ResponseEntity<Map> result = create(inputJson)

    then:
        result.statusCode == HttpStatus.BAD_REQUEST

    and:
        result.body == null

    where:
        inputJson                              || expectedErrorMessage || description
        anUnpaidOrder().withItem(ItemJsonBuilder.anItem().withUnitPrice(null)) || "" || "The clientId is null"
  }


}
