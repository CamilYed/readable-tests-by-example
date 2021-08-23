package tech.allegro.blog.vinyl.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
class AppRunner {
  public static void main(String[] args) {
    SpringApplication.run(AppRunner.class, args);
  }
}
