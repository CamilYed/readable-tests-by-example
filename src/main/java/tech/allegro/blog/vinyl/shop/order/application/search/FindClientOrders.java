package tech.allegro.blog.vinyl.shop.order.application.search;

import tech.allegro.blog.vinyl.shop.client.domain.ClientId;

public interface FindClientOrders {

  PaidClientOrdersView findPaidOrders(ClientId clientId);
}
