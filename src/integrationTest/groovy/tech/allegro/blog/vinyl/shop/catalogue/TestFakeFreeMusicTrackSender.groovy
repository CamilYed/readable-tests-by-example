package tech.allegro.blog.vinyl.shop.catalogue

import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.client.domain.ClientId

class TestFakeFreeMusicTrackSender implements FreeMusicTrackSender {

  List<ClientId> tracksSent = []

  @Override
  void send(ClientId id) {
    tracksSent.add(id)
  }
}
