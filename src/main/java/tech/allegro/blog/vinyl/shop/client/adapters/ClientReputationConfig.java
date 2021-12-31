package tech.allegro.blog.vinyl.shop.client.adapters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;

@Configuration
class ClientReputationConfig {

  @Bean
  ClientReputationProvider clientReputationProvider(ClientReputationServiceApiClient clientReputationApiClient) {
    return new HttpClientReputationProvider(clientReputationApiClient);
  }
}
