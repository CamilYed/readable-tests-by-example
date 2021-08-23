package tech.allegro.blog.vinyl.shop.delivery.domain;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import tech.allegro.blog.vinyl.shop.common.money.Money;

@FeignClient(value = "current-delivery-cost-service", url = "current-delivery-cost-service.url")
public interface CurrentDeliveryCostProvider {

  @GetMapping("/current-cost")
  Money currentCost();
}
