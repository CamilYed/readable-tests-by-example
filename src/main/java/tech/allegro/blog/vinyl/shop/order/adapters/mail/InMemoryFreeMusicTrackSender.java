package tech.allegro.blog.vinyl.shop.order.adapters.mail;

import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.order.domain.FreeMusicTrackSender;

@Slf4j
class InMemoryFreeMusicTrackSender implements FreeMusicTrackSender {

  @Override
  public void send(ClientId id) {
    log.info("Free track music was sent to the client with id: {}", id);
  }
}
