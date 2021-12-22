package tech.allegro.blog.vinyl.shop.order.application.search;

import tech.allegro.blog.vinyl.shop.order.domain.Values.OrderId;

public interface FindClientOrders {

  ClientOrdersView findOne(OrderId orderId);
}
