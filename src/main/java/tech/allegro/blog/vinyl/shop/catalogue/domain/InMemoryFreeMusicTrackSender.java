package tech.allegro.blog.vinyl.shop.catalogue.domain;

import lombok.extern.slf4j.Slf4j;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;

@Slf4j
class InMemoryFreeMusicTrackSender implements FreeMusicTrackSender {

  @Override
  public void send(ClientId id) {
    log.info("Free music track was sent to the client with id: {}", id);
  }
}
