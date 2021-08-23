package tech.allegro.blog.vinyl.shop.client.domain;

public interface ClientReputationProvider {

  ClientReputation get(ClientId clientId);
}
