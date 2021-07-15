Example project
----------------------
----------------------

## Domain Example

A store sells vinyl records.
Each order is delivered by a courier company.
When the client pays for the order, the delivery cost is charged.
The delivery cost is always getting from the supplier's system (system of courier company).
In the event of its unavailability, the delivery cost is equal to `20 EUR`.

There are two clients in the system: `STANDARD` and `VIP`.
If the order is fulfilled for the `VIP` or the `order value exceeds` the fixed amount according to the promotion price list
this order has free delivery.

For `VIP` a free track music should be sent to his mailbox.

`No modifications` can be made after the order has been paid.

### Specification

```groovy
def "shouldn't charge for delivery when the client has a VIP status"() {
  given: "There is a client order with amount 40 EUR"

  and: "The client has a VIP reputation"

  when: "When the client pays the order of 40 EUR"

  then: "The order has been paid correctly"

  and: "The payment system was notified"

  and: "The free track music was sent to the client's mailbox"
}

def "shouldn't charge for delivery for order value above fixed amount based on promotion price list"() {
  given: "There is a client order with amount 40 EUR"

  and: "The client is not a VIP"

  and: "Free delivery is valid from an amount equal to 40 EUR"

  when: "When the client pays the order of 40 EUR"

  then: "The order has been paid correctly"

  and: "The payment system was notified"

  and: "The free music track was not sent to the client's mailbox"
}

def "should charge for delivery based on price provided by courier system"() {
  given: "There is a client order with amount 40 EUR"

  and: "The client is not a VIP"

  and: "Free delivery is valid from an amount equal to 50 EUR"

  and: "The delivery costs according to the courier's price list equal to 25 EUR"

  when: "When the client pays the order of 40 EUR"

  then: "The order has been paid correctly with delivery cost equal to 25 EUR"

  and: "The payment system was notified"

  and: "The free music track was not sent to the client's mailbox"
}

def "should charge always 20 euro for delivery when the courier system is unavailable"() {
  given: "There is a client order with amount 40 EUR"

  and: "The client is not a VIP"

  and: "Free delivery is valid from an amount equal to 50 EUR"

  and: "The courier system is unavailable and default price of delivery is 20 EUR"

  when: "When the client pays the order of 40 EUR"

  then: "The order has been paid correctly with delivery cost equal to 20 EUR"

  and: "The payment system was notified"

  and: "The free music track was not sent to the client's mailbox"
}

def "shouldn't modify paid order"() {
  given: "There is a paid client order"

  when: "When the client pays the order"

  then: "The payment system was not notified"
}
```

