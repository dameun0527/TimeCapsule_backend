package com.example.timecapsule_backend.service.scheduler;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerConfig;
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

    private final CapsuleSchedulerConfig schedulerConfig;
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
                log.info("캡슐 {} 발송 처리 완료. 최종 상태: {}", capsule.getId(), capsule.getStatus());
            } catch (Exception e) {
                // dispatch 내부에서 개별 수신자별로 처리하므로, 예외가 발생하는 경우는 시스템 오류
                log.error("캡슐 {} 발송 처리 중 예기치 않은 시스템 오류 발생: {}", capsule.getId(), e.getMessage(), e);
            }
        }
    }
}