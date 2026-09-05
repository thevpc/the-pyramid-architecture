package net.thevpc.samples.petstore.drivers.notification.memory.service.impl;

import net.thevpc.samples.petstore.drivers.notification.memory.dal.api.NotificationLogRepo;
import net.thevpc.samples.petstore.drivers.notification.memory.infra.NotificationLog;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationChannel;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationStatus;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MemoryNotificationDriver implements NotificationDriver {

    private final NotificationLogRepo logRepo;

    @Autowired
    public MemoryNotificationDriver(NotificationLogRepo logRepo) {
        this.logRepo = logRepo;
    }

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return true; // supports all channels in memory
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        String id = UUID.randomUUID().toString();
        NotificationLog entry = new NotificationLog(
                id, request.getRecipient(), request.getSubject(), request.getBody(),
                request.getChannel() != null ? request.getChannel().name() : "DEFAULT",
                Instant.now()
        );
        logRepo.save(entry);

        return new NotificationResponse(id, NotificationStatus.SENT, getName(), "Delivered to in-memory store");
    }
}
