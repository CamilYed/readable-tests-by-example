package tech.allegro.blog.vinyl.shop.catalogue.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.order.domain.Events.OrderPaid;

@RequiredArgsConstructor
public class FreeMusicTrackSenderHandler {
  private final ClientReputationProvider clientReputationProvider;
  private final FreeMusicTrackSender freeMusicTrackSender;

  @EventListener
  public void handle(OrderPaid event) {
    final var clientReputation = clientReputationProvider.get(event.clientId());
    if (clientReputation.isVip()) {
      freeMusicTrackSender.send(event.clientId());
    }
  }
}
