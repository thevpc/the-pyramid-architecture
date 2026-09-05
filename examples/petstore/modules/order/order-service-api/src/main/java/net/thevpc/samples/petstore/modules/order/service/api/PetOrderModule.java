package net.thevpc.samples.petstore.modules.order.service.api;

import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Published Facade Contract for the Pet Order Module.
 */
public interface PetOrderModule {

    Order placeOrder(Order order);

    Optional<Order> findOrderById(String id);

    List<Order> findAllOrders();

    List<Order> findOrdersByStatus(OrderStatus status);

    Order updateOrderStatus(String orderId, OrderStatus status);

    boolean cancelOrder(String orderId);
}
