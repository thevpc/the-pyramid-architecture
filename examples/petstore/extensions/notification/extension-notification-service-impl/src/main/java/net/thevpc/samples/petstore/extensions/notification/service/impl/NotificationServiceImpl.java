package net.thevpc.samples.petstore.extensions.notification.service.impl;

import net.thevpc.samples.petstore.core.infra.exception.ValidationException;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationStatus;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationDriver;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Active orchestrator for the Notification Extension.
 * Manages driver routing, prioritization, and fallback.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final List<NotificationDriver> drivers;

    @Autowired
    public NotificationServiceImpl(List<NotificationDriver> drivers) {
        this.drivers = drivers != null ? drivers : Collections.emptyList();
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        if (request == null) {
            throw new ValidationException("NotificationRequest must not be null");
        }
        if (drivers.isEmpty()) {
            log.warn("No notification drivers registered, notification skipped for recipient {}", request.getRecipient());
            return new NotificationResponse(UUID.randomUUID().toString(), NotificationStatus.FAILED, "none", "No drivers registered");
        }

        // Find drivers supporting this channel sorted by priority descending
        List<NotificationDriver> candidates = drivers.stream()
                .filter(d -> d.supports(request.getChannel()))
                .sorted(Comparator.comparingInt(NotificationDriver::getPriority).reversed())
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            log.warn("No driver supports channel {}, falling back to any available driver", request.getChannel());
            candidates = new ArrayList<>(drivers);
        }

        for (NotificationDriver driver : candidates) {
            try {
                log.info("Dispatching notification via driver: {}", driver.getName());
                NotificationResponse response = driver.send(request);
                if (response != null && response.getStatus() == NotificationStatus.SENT) {
                    return response;
                }
            } catch (Exception e) {
                log.warn("Driver {} failed to send notification, trying fallback: {}", driver.getName(), e.getMessage());
            }
        }

        return new NotificationResponse(UUID.randomUUID().toString(), NotificationStatus.FAILED, "all", "All driver attempts failed");
    }

    @Override
    public List<String> getAvailableDrivers() {
        return drivers.stream().map(NotificationDriver::getName).collect(Collectors.toList());
    }
}
