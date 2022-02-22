# About
This is a project that was used to demonstrate how to write tests that can serve as living code documentation.
More information you can find on [Allegro Tech Blog](https://blog.allegro.tech/2022/02/readable-tests-by-example.html)

## Domain description

An online store sells vinyl records. Each order is delivered by a courier company cooperating with the store. 

The cost of delivery is charged when the customer pays for the order. 

The cost of delivery is always collected from the supplier's system (the courier's system). 

In the event of its unavailability (e.g. when the external courier system cannot provide the cost amount),  

we can assume that the cost of delivery is always a fixed amount of `EUR 20`.

We distinguish between two types of clients: `STANDARD` and `VIP`. 

If the order is processed for a customer with a `VIP` status or the value of the order exceeds a certain amount

according to the running promotional campaign (current price list configuration), the order will be delivered free of charge.

Additionally, for the VIP customer, a free music track should be sent to their mailbox after the payment of the order.

After paying for the order, no modifications can be made.

Such assumptions can be translated into the BDD-style scenarios:

### Example scenarios

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

    and: "Free music track was not sent to the client"
}

def "should charge for delivery based on price provided by courier system"() {
    given: "There is an unpaid order with amount 40 EUR"

    and: "The client is not a VIP"

    and: "Current delivery cost is 30 EUR"

    and: "Minimum order value for free delivery is 50 EUR"

    when: "The client makes the payment in the amount of 70 EUR"

    then: "Payment succeeded"

    and: "The client paid for delivery in the amount of 30 EUR"

    and: "Free music track was not sent to the client"
}

def "should charge always 20 euro for delivery when the courier system is unavailable"() {
    given: "There is an unpaid order with amount 40 EUR"

    and: "The client is not a VIP"

    and: "The external courier system is unavailable"

    when: "The client makes the payment in the amount of 60 EUR"

    then: "The client paid for delivery in the amount of 20 EUR"

    and: "Free music track was not sent to the client"
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

### Architecture
The structure of the code reflects the architecture that was adopted during the implementation works. The application is
a modular monolith written based on the architectural style
of [Ports & Adapter](https://blog.allegro.tech/2020/05/hexagonal-architecture-by-example.html), as well as
the [Domain Driven Design](https://www.dddcommunity.org/learning-ddd/what_is_ddd/) approach.



### Main stack technology
* Java 17
* Groovy 3.0.x (For testing purpose)
* Spring Boot 2
* Spock 2

### Testing
* All tests (unit and integration)
  ```./gradlew check```
* Unit
  ```./gradlew test```
* Integration
  ```./gradlew integrationTest``` (This command runs all tests under integrationTest source set and also acceptance tests)

