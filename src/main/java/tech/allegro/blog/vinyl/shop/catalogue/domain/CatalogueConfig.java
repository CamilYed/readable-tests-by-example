package tech.allegro.blog.vinyl.shop.catalogue.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CatalogueConfig {

  @Bean
  FreeMusicTrackSender freeMusicTrackSender() {
    return new InMemoryFreeMusicTrackSender();
  }
}
