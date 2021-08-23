package tech.allegro.blog.vinyl.shop.client.adapter;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
  value = "client-reputation-service",
  url = "${client-reputation-service.url}"
)
interface ClientReputationApiClient {

  @GetMapping("/reputation/{clientId}")
  ClientReputationJson get(@PathVariable String clientId);

  @Data
  class ClientReputationJson {
    String reputation;
    String clientId;
  }
}
