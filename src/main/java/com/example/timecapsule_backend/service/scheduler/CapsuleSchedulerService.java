package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerProperties;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleRepository;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapsuleSchedulerService {

    private final CapsuleSchedulerProperties schedulerProperties;
    private final CapsuleRepository capsuleRepository;
    private final DeliveryService deliveryService;

    @Transactional
    public void runSchedulingCycle() {
        LocalDateTime now = LocalDateTime.now();
        List<Capsule> dueCapsules = capsuleRepository.findAndLockDueForDispatch(CapsuleStatus.SCHEDULED, now);
        if (dueCapsules.isEmpty()) return;

        log.info("CapsuleSchedulerService: 처리할 캡슐 수={}", dueCapsules.size());

        for (Capsule capsule : dueCapsules) {
            try {
                deliveryService.dispatch(capsule.getId());
                log.info("캡슐 {} 자동 발송 성공", capsule.getId());
            } catch (Exception e) {
                log.error("캡슐 {} 자동 발송 실패: {}", capsule.getId(), e.getMessage(), e);
                long backOffSeconds = schedulerProperties.getBaseBackoffSeconds() * (capsule.getRetryCount() + 1);
                capsule.markFailedAttempt(backOffSeconds, schedulerProperties.getMaxRetries());
                capsuleRepository.save(capsule);
                log.info("캡슐 {} 실패 처리 후 상태={}, retryCount={}, nextAttemptAt={}",
                        capsule.getId(), capsule.getStatus(), capsule.getRetryCount(), capsule.getNextAttemptAt());
            }
        }
    }
}