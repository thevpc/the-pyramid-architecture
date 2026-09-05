package net.thevpc.samples.petstore.app.order;

import net.thevpc.nuts.app.NApp;
import net.thevpc.nuts.app.NAppRun;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@NApp
@SpringBootApplication(scanBasePackages = {
        "net.thevpc.samples.petstore.modules.order",
        "net.thevpc.samples.petstore.modules.catalog.service.restcli",
        "net.thevpc.samples.petstore.extensions.notification.service.restcli",
        "net.thevpc.samples.petstore.core"
})
@EntityScan(basePackages = {
        "net.thevpc.samples.petstore.modules.order",
        "net.thevpc.samples.petstore.core"
})
@EnableJpaRepositories(basePackages = {
        "net.thevpc.samples.petstore.modules.order",
        "net.thevpc.samples.petstore.core"
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @NAppRun
    public void run() {
        System.out.println("Order Microservice started on port 8082.");
    }
}
