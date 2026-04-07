package com.example.timecapsule_backend.config.email;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "performance.test")
public class PerformanceTestConfig {

    private int maxEmailCount = 1000;
    private int defaultEmailCount = 10;
}
