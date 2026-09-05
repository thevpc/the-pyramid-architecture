package net.thevpc.samples.petstore.modules.order.service.impl.entity;

import net.thevpc.samples.petstore.core.infra.exception.NotFoundException;
import net.thevpc.samples.petstore.core.infra.util.AppValidation;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import net.thevpc.samples.petstore.core.interceptor.impl.InterceptorDispatcher;
import net.thevpc.samples.petstore.modules.order.dal.api.OrderRepository;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import net.thevpc.samples.petstore.modules.order.infra.OrderItem;
import net.thevpc.samples.petstore.modules.order.infra.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Exclusive repository owner for Order within the Order module.
 */
@Service
public class OrderCrudService {

    private final OrderRepository orderRepository;
    private final InterceptorDispatcher interceptorDispatcher;

    @Autowired
    public OrderCrudService(OrderRepository orderRepository, InterceptorDispatcher interceptorDispatcher) {
        this.orderRepository = orderRepository;
        this.interceptorDispatcher = interceptorDispatcher;
    }

    public Order placeOrder(Order order) {
        AppValidation.requireNonNull(order, "Order must not be null");
        AppValidation.requireTrue(order.getItems() != null && !order.getItems().isEmpty(),
                "Order must contain at least one item");

        if (order.getId() == null || order.getId().isBlank()) {
            order.setId(UUID.randomUUID().toString());
        }
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PLACED);
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(Instant.now());
        }

        // Calculate total if not set
        if (order.getTotalAmount() == null) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                if (item.getPrice() != null) {
                    total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
            order.setTotalAmount(total);
        }

        // 1. Dispatch BEFORE_ADD (interceptors validate availability across modules)
        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_ADD, order, null);

        // 2. Persist
        Order saved = orderRepository.save(order);

        // 3. Dispatch AFTER_ADD (synchronize pet status, send notifications)
        interceptorDispatcher.dispatch(LifecyclePhase.AFTER_ADD, saved, null);

        return saved;
    }

    public Optional<Order> findOrderById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status != null ? status : OrderStatus.PLACED);
    }

    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        Order oldCopy = new Order(order.getId(), order.getCustomerId(), order.getItems(), order.getStatus(), order.getTotalAmount(), order.getOrderDate());
        order.setStatus(status);

        interceptorDispatcher.dispatch(LifecyclePhase.BEFORE_UPDATE, order, oldCopy);
        Order saved = orderRepository.save(order);
        interceptorDispatcher.dispatch(LifecyclePhase.AFTER_UPDATE, saved, oldCopy);

        return saved;
    }

    public boolean cancelOrder(String orderId) {
        Optional<Order> opt = orderRepository.findById(orderId);
        if (opt.isEmpty()) {
            return false;
        }
        updateOrderStatus(orderId, OrderStatus.CANCELLED);
        return true;
    }
}
