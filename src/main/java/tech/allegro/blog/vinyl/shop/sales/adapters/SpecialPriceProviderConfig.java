package tech.allegro.blog.vinyl.shop.sales.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.sales.domain.SpecialPriceProvider;

@Configuration
class SpecialPriceProviderConfig {

  @Bean
  SpecialPriceProvider specialPriceProvider(SpecialPriceCatalogueApiClient specialPriceCatalogueApiClient) {
    return new HttpSpecialPriceProvider(specialPriceCatalogueApiClient);
  }
}
