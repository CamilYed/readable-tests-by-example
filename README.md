Example project
----------------------
----------------------

## Example Domain

A store sells vinyl records. Each order is delivered by a courier company. When the client pays for the order, the
delivery cost is charged. The delivery cost is always getting from the supplier's system (system of courier company). In
the event of its unavailability, the delivery cost is equal to `20 EUR`.

There are two clients in the system: `STANDARD` and `VIP`. If the order is fulfilled for the `VIP` or
the `order value exceeds` the fixed amount according to the promotion price list this order has free delivery.

For `VIP` a free track music should be sent to his mailbox.

`No modifications` can be made after the order has been paid.

### Specification

```groovy
def "shouldn't charge for delivery when the client has a VIP status"() {
    given: "There is an unpaid order"

    and: "The Client is a VIP"

    when: "The client makes the payment"

    then: "Payment succeeded"

    and: "The client did not paid for delivery"

    and: "Free music track was sent to the client"
}

def "shouldn't charge for delivery when order value is above fixed amount based on promotion price list"() {
    given: "There is an unpaid order with amount 40 EUR"

    and: "The client is not a VIP"

    and: "Minimum order value for free delivery is 40 EUR"

    when: "The client makes the payment"

    then: "Payment succeeded"

    and: "The client did not paid for delivery"

    and: "Free track music was not sent to the client"
}

def "should charge for delivery based on price provided by courier system"() {
    given: "There is an unpaid order with amount 40 EUR"

    and: "The client is not a VIP"

    and: "Current delivery cost is 30 EUR"

    and: "Minimum order value for free delivery is 50 EUR"

    when: "The client makes the payment in the amount of 70 EUR"

    then: "Payment succeeded"

    and: "The client paid for delivery in the amount of 30 EUR"

    and: "Free track music was not sent to the client"
}

def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given: "There is an unpaid order with amount 40 EUR"

    and: "The client is not a VIP"

    and: "The external courier system is unavailable"

    when: "The client makes the payment in the amount of 60 EUR"

    then: "The client paid for delivery in the amount of 20 EUR"

    and: "Free track music was not sent to the client"
}

def "shouldn't accept payment if the amounts differ"() {
    given: "There is an unpaid order with amount 10 EUR"

    and: "Current delivery cost is 30 EUR"

    when: "The client makes the payment in the amount of 39.00 EUR"

    then: "Payment failed due to different amounts"
}

def "shouldn't modify paid order"() {
    given: "There is a paid order"

    when: "Client changes item quantity"

    then: "Change failed due order already paid"
}
```

