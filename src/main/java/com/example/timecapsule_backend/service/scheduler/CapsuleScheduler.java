package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class CapsuleScheduler {

    private final CapsuleSchedulerConfig capsuleSchedulerConfig;
    private final CapsuleSchedulerService capsuleSchedulerService;

    @Scheduled(fixedDelayString = "#{@capsuleSchedulerConfig.delay}")
    public void scheduledTrigger() {
        log.info("CapsuleScheduler triggered at {}", LocalDateTime.now());
        capsuleSchedulerService.runSchedulingCycle();
    }
}