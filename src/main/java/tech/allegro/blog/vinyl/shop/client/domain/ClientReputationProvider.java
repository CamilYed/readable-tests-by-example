package tech.allegro.blog.vinyl.shop.client.domain;

public interface ClientReputationProvider {

  ClientReputation getFor(ClientId clientId);
}
