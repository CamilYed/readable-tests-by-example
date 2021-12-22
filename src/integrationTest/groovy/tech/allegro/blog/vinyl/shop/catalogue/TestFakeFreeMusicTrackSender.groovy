package tech.allegro.blog.vinyl.shop.catalogue

import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.client.domain.ClientId

class TestFakeFreeMusicTrackSender implements FreeMusicTrackSender {

  List<ClientId> recordedRequests = []

  @Override
  void send(ClientId id) {
    recordedRequests.add(id)
  }
}
