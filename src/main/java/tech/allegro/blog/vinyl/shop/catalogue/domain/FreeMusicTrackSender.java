package tech.allegro.blog.vinyl.shop.catalogue.domain;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;

public interface FreeMusicTrackSender {

  void send(ClientId id);
}
