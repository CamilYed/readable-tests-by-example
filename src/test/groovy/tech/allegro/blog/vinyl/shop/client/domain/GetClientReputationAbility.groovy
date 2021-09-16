package tech.allegro.blog.vinyl.shop.client.domain

trait GetClientReputationAbility {

    final ClientReputationProvider clientReputationProvider = new InMemoryClientReputationProvider()

    void clientWithIdIsVip(String id) {
        ClientId clientId = ClientId.of(id)
        clientReputationProvider.setReputation([(clientId): ClientReputation.of(clientId, ClientReputation.Type.VIP)])
    }

    void clientWithIdIsStandard(ClientId clientId) {
        clientReputationProvider.setReputation([(clientId): ClientReputation.of(clientId, ClientReputation.Type.STANDARD)])
    }
}
