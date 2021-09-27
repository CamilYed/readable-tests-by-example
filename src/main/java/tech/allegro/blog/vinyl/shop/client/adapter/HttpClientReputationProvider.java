package tech.allegro.blog.vinyl.shop.client.adapter;

import lombok.RequiredArgsConstructor;
import tech.allegro.blog.vinyl.shop.client.adapter.ClientReputationServiceApiClient.ClientReputationJson;
import tech.allegro.blog.vinyl.shop.client.domain.ClientId;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputation.Type;
import tech.allegro.blog.vinyl.shop.client.domain.ClientReputationProvider;

@RequiredArgsConstructor
class HttpClientReputationProvider implements ClientReputationProvider {
  private final ClientReputationServiceApiClient apiClient;

  @Override
  public ClientReputation get(ClientId clientId) {
    ClientReputationJson clientReputationJson = apiClient.get(clientId.value());
    return toDomain(clientReputationJson);
  }

  private ClientReputation toDomain(ClientReputationJson json) {
    return ClientReputation.of(new ClientId(json.clientId), Type.valueOf(json.getReputation()));
  }
}
