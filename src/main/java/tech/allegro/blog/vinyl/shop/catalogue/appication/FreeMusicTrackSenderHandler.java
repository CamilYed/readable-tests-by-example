package tech.allegro.blog.vinyl.shop.catalogue.appication;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;
import tech.allegro.blog.vinyl.shop.order.domain.OrderDomainEvents.OrderPaid;

@RequiredArgsConstructor
public class FreeMusicTrackSenderHandler {
  private final ClientReputationProvider clientReputationProvider;
  private final FreeMusicTrackSender freeMusicTrackSender;

  @EventListener
  public void handle(OrderPaid event) {
    final var clientReputation = clientReputationProvider.get(event.getClientId());
    if (clientReputation.isVip()) {
      freeMusicTrackSender.send(event.getClientId());
    }
  }
}
