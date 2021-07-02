package tech.allegro.blog.vinyl.shop.client;

public interface ClientReputationProvider {

  ClientReputation getFor(ClientId clientId);
}
