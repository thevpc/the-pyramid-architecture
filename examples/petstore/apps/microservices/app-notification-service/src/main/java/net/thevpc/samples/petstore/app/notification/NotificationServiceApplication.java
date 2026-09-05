package net.thevpc.samples.petstore.app.notification;

import net.thevpc.nuts.app.NApp;
import net.thevpc.nuts.app.NAppRun;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@NApp
@SpringBootApplication(scanBasePackages = {
        "net.thevpc.samples.petstore.extensions.notification",
        "net.thevpc.samples.petstore.drivers.notification",
        "net.thevpc.samples.petstore.core"
})
@EntityScan(basePackages = {
        "net.thevpc.samples.petstore.extensions.notification",
        "net.thevpc.samples.petstore.drivers.notification",
        "net.thevpc.samples.petstore.core"
})
@EnableJpaRepositories(basePackages = {
        "net.thevpc.samples.petstore.extensions.notification",
        "net.thevpc.samples.petstore.drivers.notification",
        "net.thevpc.samples.petstore.core"
})
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @NAppRun
    public void run() {
        System.out.println("Notification Microservice started on port 8083.");
    }
}
