package tech.allegro.blog.vinyl.shop.client.domain;

import lombok.Value;

@Value(staticConstructor = "of")
public class ClientReputation {
  ClientId clientId;
  Type type;

  public enum Type {
    STANDARD, VIP
  }

  public static ClientReputation vip(ClientId clientId) {
    return new ClientReputation(clientId, Type.VIP);
  }

  public static ClientReputation standard(ClientId clientId) {
    return new ClientReputation(clientId, Type.STANDARD);
  }

  public boolean isVip() {
    return type == Type.VIP;
  }
}
