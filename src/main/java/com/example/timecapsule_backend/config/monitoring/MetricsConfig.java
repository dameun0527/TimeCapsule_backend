package com.example.timecapsule_backend.config.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@RequiredArgsConstructor
public class MetricsConfig {

    private final MeterRegistry meterRegistry;

}