package net.thevpc.samples.petstore.extensions.notification.service.api;

import net.thevpc.samples.petstore.extensions.notification.infra.NotificationChannel;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;

/**
 * Driver SPI implemented by infrastructure drivers or business modules.
 */
public interface NotificationDriver {

    String getName();

    boolean supports(NotificationChannel channel);

    NotificationResponse send(NotificationRequest request);

    default int getPriority() {
        return 0;
    }
}
