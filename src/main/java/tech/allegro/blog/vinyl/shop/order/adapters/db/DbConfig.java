package tech.allegro.blog.vinyl.shop.order.adapters.db;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.allegro.blog.vinyl.shop.order.domain.OrderRepository;

@Configuration
class DbConfig {

  @Bean
  OrderRepository orderRepository() {
    return new ImMemoryDatabase();
  }
}
