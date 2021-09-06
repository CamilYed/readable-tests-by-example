package tech.allegro.blog.vinyl.shop.catalogue.appication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;

@Configuration
class CatalogueHandlerConfig {

  @Bean
  FreeMusicTrackSenderHandler freeMusicTrackSenderHandler(ClientReputationProvider clientReputationProvider,
                                                          FreeMusicTrackSender freeMusicTrackSender) {
    return new FreeMusicTrackSenderHandler(clientReputationProvider, freeMusicTrackSender);
  }
}
