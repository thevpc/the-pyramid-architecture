package net.thevpc.samples.petstore.extensions.notification.service.restcli;

import net.thevpc.samples.petstore.core.infra.annotation.Generated;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationRequest;
import net.thevpc.samples.petstore.extensions.notification.infra.NotificationResponse;
import net.thevpc.samples.petstore.extensions.notification.service.api.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@Generated("pyramid")
public class NotificationServiceRestCli implements NotificationService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    @Autowired
    public NotificationServiceRestCli(
            @Autowired(required = false) RestTemplate restTemplate,
            @Value("${petstore.extensions.notification.url:http://localhost:8080/api/notifications}") String baseUrl) {
        this.restTemplate = restTemplate != null ? restTemplate : new RestTemplate();
        this.baseUrl = baseUrl;
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        return restTemplate.postForObject(baseUrl + "/send", request, NotificationResponse.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getAvailableDrivers() {
        String[] drivers = restTemplate.getForObject(baseUrl + "/drivers", String[].class);
        return drivers != null ? Arrays.asList(drivers) : List.of();
    }
}
