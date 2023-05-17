package com.frontbackend.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);

        System.out.println("CHAFA SIAE");
        System.out.println("URL: http://localhost:5013/swagger-ui.html#");
    }
}
