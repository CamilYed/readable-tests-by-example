package tech.allegro.blog.vinyl.shop.sales.adapters;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tech.allegro.blog.vinyl.shop.common.money.MoneyJson;

@FeignClient(
  value = "special-price-catalogue-service",
  url = "${special-price-catalogue-service.url}"
)
interface SpecialPriceCatalogueApiClient {

  @GetMapping("/promotion/{id}")
  MoneyJson getSpecialPrice(@PathVariable String id);
}
