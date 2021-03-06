package tech.allegro.blog.vinyl.shop.client.domain


import java.util.concurrent.ConcurrentHashMap

class InMemoryClientReputationProvider implements ClientReputationProvider {

  private final Map<ClientId, ClientReputation> reputations = new ConcurrentHashMap<>()

  @Override
  ClientReputation get(ClientId clientId) {
    return reputations.get(clientId)
  }

  void setReputation(Map<ClientId, ClientReputation> reputation) {
    reputations.putAll(reputation)
  }
}
