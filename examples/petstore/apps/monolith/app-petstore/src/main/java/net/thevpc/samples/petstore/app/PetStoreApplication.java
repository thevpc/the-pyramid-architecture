package net.thevpc.samples.petstore.app;

import net.thevpc.nuts.app.NApp;
import net.thevpc.nuts.app.NAppRun;
import net.thevpc.nuts.io.NPrintStream;
import net.thevpc.nuts.text.NMsg;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@NApp
@SpringBootApplication(scanBasePackages = "net.thevpc.samples.petstore")
@EntityScan(basePackages = "net.thevpc.samples.petstore")
@EnableJpaRepositories(basePackages = "net.thevpc.samples.petstore")
public class PetStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetStoreApplication.class, args);
    }

    @NAppRun
    public void run() {
        NPrintStream out = NPrintStream.of(System.out);
        out.println(NMsg.ofC("##Pyramid PetStore## reference application is active."));
    }
}
