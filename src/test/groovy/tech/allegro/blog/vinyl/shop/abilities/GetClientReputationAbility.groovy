package tech.allegro.blog.vinyl.shop.abilities

import tech.allegro.blog.vinyl.shop.TestData
import tech.allegro.blog.vinyl.shop.client.domain.ClientId
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider
import tech.allegro.blog.vinyl.shop.client.domain.InMemoryClientReputationProvider

trait GetClientReputationAbility {

  static final ClientReputationProvider clientReputationProvider = new InMemoryClientReputationProvider()

  void clientIsVip() {
    clientWithIdIsVip(TestData.CLIENT_ID)
  }

  void clientIsNotVip() {
    clientWithIdIsStandard(TestData.CLIENT_ID)
  }

  void clientWithIdIsVip(String id) {
    ClientId clientId = new ClientId(id)
    clientReputationProvider.setReputation([(clientId): ClientReputation.of(clientId, ClientReputation.Type.VIP)])
  }

  void clientWithIdIsStandard(String id) {
    ClientId clientId = new ClientId(id)
    clientReputationProvider.setReputation([(clientId): ClientReputation.of(clientId, ClientReputation.Type.STANDARD)])
  }
}
