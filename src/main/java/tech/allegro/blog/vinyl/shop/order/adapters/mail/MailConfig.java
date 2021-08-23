package tech.allegro.blog.vinyl.shop.order.adapters.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.order.domain.FreeMusicTrackSender;

@Configuration
class MailConfig {

  @Bean
  FreeMusicTrackSender freeMusicTrackSender() {
    return new InMemoryFreeMusicTrackSender();
  }
}
