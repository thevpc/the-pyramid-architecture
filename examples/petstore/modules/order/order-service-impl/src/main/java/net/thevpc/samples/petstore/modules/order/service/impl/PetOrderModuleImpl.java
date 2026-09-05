package net.thevpc.samples.petstore.modules.order.service.impl;

import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import net.thevpc.samples.petstore.modules.order.service.api.PetOrderModule;
import net.thevpc.samples.petstore.modules.order.service.impl.entity.OrderCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Local implementation of PetOrderModule facade.
 * Delegates to internal OrderCrudService.
 */
@Service("petOrderModule")
public class PetOrderModuleImpl implements PetOrderModule {

    private final OrderCrudService orderCrudService;

    @Autowired
    public PetOrderModuleImpl(OrderCrudService orderCrudService) {
        this.orderCrudService = orderCrudService;
    }

    @Override
    public Order placeOrder(Order order) {
        return orderCrudService.placeOrder(order);
    }

    @Override
    public Optional<Order> findOrderById(String id) {
        return orderCrudService.findOrderById(id);
    }

    @Override
    public List<Order> findAllOrders() {
        return orderCrudService.findAllOrders();
    }

    @Override
    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderCrudService.findOrdersByStatus(status);
    }

    @Override
    public Order updateOrderStatus(String orderId, OrderStatus status) {
        return orderCrudService.updateOrderStatus(orderId, status);
    }

    @Override
    public boolean cancelOrder(String orderId) {
        return orderCrudService.cancelOrder(orderId);
    }
}
