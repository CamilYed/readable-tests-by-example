package tech.allegro.blog.vinyl.shop.delivery.adpater;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.delivery.domain.DeliveryCostProvider;

@Configuration
class DeliveryCostProviderConfig {

  @Bean
  DeliveryCostProvider currentDeliveryCostProvider(DeliveryCostServiceApiClient deliveryCostProviderApiClient) {
    return new HttpCurrentDeliveryCostProvider(deliveryCostProviderApiClient);
  }
}
