package net.thevpc.samples.petstore.modules.order.service.impl.interceptor;

import net.thevpc.samples.petstore.core.interceptor.api.AppEntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.EntityInterceptor;
import net.thevpc.samples.petstore.core.interceptor.api.InterceptorEvent;
import net.thevpc.samples.petstore.core.interceptor.api.LifecyclePhase;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationChannel;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationService;
import net.thevpc.samples.petstore.modules.order.infra.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Cross-cutting notification interceptor.
 * Executes on AFTER_ADD of Order.
 * Calls the Notification Extension contract without knowing which driver is active.
 */
@Component
@AppEntityInterceptor(types = Order.class, phases = LifecyclePhase.AFTER_ADD, order = 10)
public class OrderNotificationInterceptor implements EntityInterceptor<Order> {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationInterceptor.class);
    private final NotificationService notificationService;

    @Autowired
    public OrderNotificationInterceptor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public Class<Order> getEntityType() {
        return Order.class;
    }

    @Override
    public LifecyclePhase[] getPhases() {
        return new LifecyclePhase[]{LifecyclePhase.AFTER_ADD};
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    public void onEvent(InterceptorEvent<Order> event) {
        Order order = event.getEntity();
        String recipient = order.getCustomerId() != null ? order.getCustomerId() : "customer@petstore.org";
        String subject = "Order Confirmation #" + order.getId();
        String body = String.format("Thank you for your order! Total amount: $%s. Items: %d",
                order.getTotalAmount(), order.getItems().size());

        NotificationRequest request = new NotificationRequest(recipient, subject, body, NotificationChannel.CONSOLE);
        try {
            notificationService.sendNotification(request);
            log.info("OrderNotificationInterceptor: Notification dispatched for order {}", order.getId());
        } catch (Exception e) {
            log.error("OrderNotificationInterceptor: Notification failed for order {}", order.getId(), e);
        }
    }
}
