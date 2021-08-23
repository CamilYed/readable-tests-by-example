package tech.allegro.blog.vinyl.shop.order.domain;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;

public interface FreeMusicTrackSender {

  void send(ClientId id);
}
