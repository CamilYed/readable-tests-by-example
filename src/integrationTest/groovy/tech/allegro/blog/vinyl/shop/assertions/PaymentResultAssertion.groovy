package tech.allegro.blog.vinyl.shop.assertions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class PaymentResultAssertion {
    private final ResponseEntity<Map> paymentResult

    static PaymentResultAssertion assertThatPayment(ResponseEntity<Map> response) {
        return new PaymentResultAssertion(response)
    }

    private PaymentResultAssertion(ResponseEntity<Map> response) {
        this.paymentResult = response
    }

    PaymentResultAssertion madeSuccessfully() {
        assert paymentResult.statusCode == HttpStatus.ACCEPTED
        return this
    }
}
