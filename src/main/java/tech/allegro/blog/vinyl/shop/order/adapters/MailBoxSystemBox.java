package tech.allegro.blog.vinyl.shop.order.adapters;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;

public interface MailBoxSystemBox {

  void sendFreeMusicTrackForClient(ClientId id);
}
