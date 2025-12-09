package cl.usach.mis.sigpheapp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SigpheAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SigpheAppBackendApplication.class, args);
    }

}
