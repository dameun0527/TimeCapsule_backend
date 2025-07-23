package com.example.timecapsule_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TimeCapsuleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeCapsuleBackendApplication.class, args);
    }

}
