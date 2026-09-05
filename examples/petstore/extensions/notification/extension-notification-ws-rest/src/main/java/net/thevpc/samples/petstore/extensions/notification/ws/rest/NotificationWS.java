package net.thevpc.samples.petstore.extensions.notification.ws.rest;

import net.thevpc.samples.petstore.core.infra.annotation.AppModuleWS;
import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AppModuleWS(NotificationService.class)
@Generated("pyramid")
public class NotificationWS {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.sendNotification(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/drivers")
    public ResponseEntity<List<String>> getAvailableDrivers() {
        return ResponseEntity.ok(notificationService.getAvailableDrivers());
    }
}
