package tech.allegro.blog.vinyl.shop.order.api;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.common.json.FailureJson;
import tech.allegro.blog.vinyl.shop.common.money.Money;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler;
import tech.allegro.blog.vinyl.shop.order.application.OrderPaymentHandler.PayOrderCommand;
import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

@RestController
class OrderPaymentsEndpoint {
  private static final Logger log = org.slf4j.LoggerFactory.getLogger(OrderPaymentsEndpoint.class);
  private final OrderPaymentHandler paymentHandler;

  public OrderPaymentsEndpoint(OrderPaymentHandler paymentHandler) {
    this.paymentHandler = paymentHandler;
  }

  @PutMapping(value = "/orders/{orderId}/payment", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Void> payments(@PathVariable String orderId, @RequestBody PayOrderJson payOrderJson) {
    final var command = payOrderJson.toCommand(orderId);
    paymentHandler.handle(command);
    return ResponseEntity.accepted().build();
  }

  static class PayOrderJson {
    String clientId;
    MoneyJson cost;

    public PayOrderJson() {
    }

    PayOrderCommand toCommand(String orderId) {
      return PayOrderCommand.of(
        ClientId.of(clientId),
        OrderId.of(orderId),
        Money.of(cost.getAmount(), cost.getCurrency())
      );
    }

    public String getClientId() {
      return this.clientId;
    }

    public MoneyJson getCost() {
      return this.cost;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public void setCost(MoneyJson cost) {
      this.cost = cost;
    }

    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof PayOrderJson)) return false;
      final PayOrderJson other = (PayOrderJson) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$clientId = this.getClientId();
      final Object other$clientId = other.getClientId();
      if (this$clientId == null ? other$clientId != null : !this$clientId.equals(other$clientId)) return false;
      final Object this$cost = this.getCost();
      final Object other$cost = other.getCost();
      if (this$cost == null ? other$cost != null : !this$cost.equals(other$cost)) return false;
      return true;
    }

    protected boolean canEqual(final Object other) {
      return other instanceof PayOrderJson;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $clientId = this.getClientId();
      result = result * PRIME + ($clientId == null ? 43 : $clientId.hashCode());
      final Object $cost = this.getCost();
      result = result * PRIME + ($cost == null ? 43 : $cost.hashCode());
      return result;
    }

    public String toString() {
      return "OrderPaymentsEndpoint.PayOrderJson(clientId=" + this.getClientId() + ", cost=" + this.getCost() + ")";
    }
  }

  @ExceptionHandler(Throwable.class)
  ResponseEntity<FailureJson> handleUnexpectedError(Throwable e) {
    log.error("An unexpected error occurred during payment", e);
    FailureJson errorMessage = new FailureJson(e.getMessage());
    return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
