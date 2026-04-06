package com.example.timecapsule_backend.config.email;

import com.example.timecapsule_backend.service.email.EmailMode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "email.delivery")
public class EmailDeliveryConfig {

    private EmailMode defaultStrategy = EmailMode.ASYNC;
}
