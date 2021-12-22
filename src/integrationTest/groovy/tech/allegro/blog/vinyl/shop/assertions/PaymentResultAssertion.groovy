package tech.allegro.blog.vinyl.shop.assertions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

class PaymentResultAssertion {
  private final ResponseEntity<Map> paymentResult

  static PaymentResultAssertion assertThat(ResponseEntity<Map> response) {
    return new PaymentResultAssertion(response)
  }

  private PaymentResultAssertion(ResponseEntity<Map> response) {
    this.paymentResult = response
  }

  PaymentResultAssertion succeeded() {
    assert paymentResult.statusCode == HttpStatus.ACCEPTED
    return this
  }

  PaymentResultAssertion failed() {
    assert !paymentResult.statusCode.is2xxSuccessful()
    return this
  }

  PaymentResultAssertion dueToDifferentAmounts(String message) {
    assert paymentResult.statusCode == HttpStatus.UNPROCESSABLE_ENTITY
    assert paymentResult.body.message == message
    return this
  }
}
