package tech.allegro.blog.vinyl.shop.delivery.adpater;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.delivery.domain.CurrentDeliveryCostProvider;

@Configuration
class DeliveryCostProviderConfig {

  @Bean
  CurrentDeliveryCostProvider currentDeliveryCostProvider(DeliveryCostProviderApiClient deliveryCostProviderApiClient) {
    return new HttpCurrentDeliveryCostProvider(deliveryCostProviderApiClient);
  }
}
