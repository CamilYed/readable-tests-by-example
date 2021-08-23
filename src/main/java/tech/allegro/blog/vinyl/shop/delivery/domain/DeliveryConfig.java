package tech.allegro.blog.vinyl.shop.delivery.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider;

@Configuration
class DeliveryConfig {

  @Bean
  DeliveryCostPolicy deliveryCostPolicy(CurrentDeliveryCostProvider currentDeliveryCostProvider,
                                        SpecialPriceProvider promotionPriceCatalogue) {
    return new DeliveryCostPolicy.DefaultDeliveryCostPolicy(currentDeliveryCostProvider, promotionPriceCatalogue);
  }
}
