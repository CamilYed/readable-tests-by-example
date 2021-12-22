package tech.allegro.blog.vinyl.shop.client.adapter;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
  value = "client-reputation-service",
  url = "${client-reputation-service.url}"
)
interface ClientReputationServiceApiClient {

  @GetMapping(value = "/reputation/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
  ClientReputationJson get(@PathVariable String clientId);

  @Data
  class ClientReputationJson {
    String reputation;
    String clientId;
  }
}
