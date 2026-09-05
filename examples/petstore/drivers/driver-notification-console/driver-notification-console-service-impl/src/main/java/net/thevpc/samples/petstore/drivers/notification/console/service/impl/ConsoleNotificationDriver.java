package net.thevpc.samples.petstore.drivers.notification.console.service.impl;

import net.thevpc.nuts.text.NMsg;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationChannel;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationStatus;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationDriver;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsoleNotificationDriver implements NotificationDriver {

    @Override
    public String getName() {
        return "console";
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.CONSOLE;
    }

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        String id = UUID.randomUUID().toString();
        // Rich formatted console message using Nuts NMsg
        String formatted = NMsg.ofC(
                "[Nuts Console Driver] To: %s | Subject: %s | Body: %s",
                request.getRecipient(), request.getSubject(), request.getBody()
        ).toString();

        System.out.println(formatted);

        return new NotificationResponse(id, NotificationStatus.SENT, getName(), "Logged to console");
    }
}
