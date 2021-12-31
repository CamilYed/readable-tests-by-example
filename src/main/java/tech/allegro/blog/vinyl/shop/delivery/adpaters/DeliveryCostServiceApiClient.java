package tech.allegro.blog.vinyl.shop.delivery.adpaters;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;

@FeignClient(
  value = "delivery-cost-service",
  url = "${delivery-cost-service.url}"
)
interface DeliveryCostServiceApiClient {

  @GetMapping("/current-cost")
  MoneyJson currentCost();
}
