package tech.allegro.blog.vinyl.shop.stubs

import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider

import java.util.concurrent.ConcurrentHashMap

class FakeClientReputationProvider implements ClientReputationProvider {
    Map<ClientId, ClientReputation> reputations = new ConcurrentHashMap<>()

    @Override
    ClientReputation get(ClientId clientId) {
        return reputations.get(clientId)
    }
}
