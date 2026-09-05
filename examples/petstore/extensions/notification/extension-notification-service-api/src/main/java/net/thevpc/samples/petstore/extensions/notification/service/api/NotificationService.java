package net.thevpc.samples.petstore.extensions.notification.service.api;

import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;

import java.util.List;

/**
 * Published Extension contract for sending notifications.
 * Business modules depend strictly on this interface.
 */
public interface NotificationService {

    NotificationResponse sendNotification(NotificationRequest request);

    List<String> getAvailableDrivers();
}
