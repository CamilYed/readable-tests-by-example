package tech.allegro.blog.vinyl.shop.catalogue.domain;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
// TODO consider another module ( IMO It should not be the responsibility of this module )
public interface FreeMusicTrackSender {

  void send(ClientId id);
}
