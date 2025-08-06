package com.example.timecapsule_backend.config.scheduler;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class CapsuleSchedulerProperties {

    @Value("${scheduler.capsule.delay:60000}")
    private long delay;

    @Value("${scheduler.capsule.max-retries:3}")
    private int maxRetries;

    @Value("${scheduler.capsule.base-backoff-seconds:30}")
    private long baseBackoffSeconds;

}
