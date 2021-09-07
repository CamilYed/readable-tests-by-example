package tech.allegro.blog.vinyl.shop.assertions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class OrderCreationAssertion {
    private ResponseEntity<Map> creationResult

    private OrderCreationAssertion(ResponseEntity<Map> response) {
        this.creationResult = response
    }

    static assertThatOrder(ResponseEntity<Map> response) {
        return new OrderCreationAssertion(response)
    }

    OrderCreationAssertion succeeded() {
        assert creationResult.statusCode == HttpStatus.CREATED
        return this
    }


}
