package tech.allegro.blog.vinyl.shop.assertions

import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.SpyBean
import spock.util.concurrent.PollingConditions
import tech.allegro.blog.vinyl.shop.catalogue.domain.FreeMusicTrackSender
import tech.allegro.blog.vinyl.shop.client.domain.ClientId

import static org.mockito.Mockito.times

trait FreeTrackMusicSenderAssertion {

    @SpyBean
    private FreeMusicTrackSender freeMusicTrackSender

    private PollingConditions pollingConditions = new PollingConditions(timeout: 1)

    void assertThatFreeMusicTrackWasSentToClientOnce(String clientId) {
        pollingConditions.eventually {
            Mockito.verify(freeMusicTrackSender, times(1))
                .send(ClientId.of(clientId))
        }
    }
}
