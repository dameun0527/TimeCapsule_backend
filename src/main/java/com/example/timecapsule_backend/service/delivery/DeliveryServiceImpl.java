package com.example.timecapsule_backend.service.delivery;

import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleRecipient;
import com.example.timecapsule_backend.domain.capsule.CapsuleRepository;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLog;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLogRepository;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import com.example.timecapsule_backend.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final CapsuleRepository capsuleRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final EmailService emailService;

    @Override
    public List<Long> findDueCapsuleIds(LocalDateTime now) {
        return capsuleRepository
                .findByStatusAndScheduledAtBefore(CapsuleStatus.SCHEDULED, now)
                .stream()
                .map(Capsule::getId)
                .toList();
    }

    @Override
    @Transactional
    public void dispatch(Long capsuleId) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));

        if (capsule.getStatus() != CapsuleStatus.SCHEDULED) {
            return;
        }

        try {
            for (CapsuleRecipient recipient : capsule.getRecipients()) {
                emailService.sendTimeCapsuleEmail(
                        recipient.getUser().getEmail(),
                        capsule.getContent().getTitle(),
                        capsule.getContent().getMainMessage(),
                        capsule.getTheme() != null ? capsule.getTheme().getThemeType() : null
                );
            }
            capsule.markDelivered();
            deliveryLogRepository.save(
                    DeliveryLog.builder()
                            .capsule(capsule)
                            .capsuleStatus(capsule.getStatus())
                            .attemptedAt(LocalDateTime.now())
                            .build());
            log.info("캡슐 {} 발송 성공, 상태를 DELIVERED로 변경함.", capsuleId);
        } catch (BusinessException be) {
            capsule.markFailed();
            deliveryLogRepository.save(
                    DeliveryLog.builder()
                            .capsule(capsule)
                            .capsuleStatus(capsule.getStatus())
                            .attemptedAt(LocalDateTime.now())
                            .build());
            log.warn("캡슐 {} 발송 중 비즈니스 오류 발생: {}. 상태를 FAILED로 변경함.", capsuleId, be.getMessage());
            throw be;
        } catch (Exception e) {
            capsule.markFailed();
            deliveryLogRepository.save(
                    DeliveryLog.builder()
                            .capsule(capsule)
                            .capsuleStatus(capsule.getStatus())
                            .attemptedAt(LocalDateTime.now())
                            .build());
            log.error("캡슐 {} 발송 중 예기치 않은 오류 발생. 상태를 FAILED로 변경함.", capsuleId, e);
        }
    }

    @Override
    @Transactional
    public void changeStatus(Long capsuleId, CapsuleStatus status) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));
        switch (status) {
            case CANCELLED -> capsule.cancel();
            case DELIVERED -> capsule.markDelivered();
            case FAILED -> capsule.markFailed();
            default -> throw new BusinessException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
    }

    @Override
    public List<DeliveryLogResponse> getDeliveryLogs(Long userId, Long capsuleId) {
        capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));

        return deliveryLogRepository.findByCapsuleId(capsuleId).stream()
                .map(DeliveryLogResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void processBatchDelivery(List<Long> capsuleIds) {
        log.info("배치 발송 시작. 대상 캡슐 수: {}", capsuleIds.size());

        for (Long capsuleId : capsuleIds) {
            try {
                dispatch(capsuleId);
            } catch (Exception e) {
                log.error("캡슐 {} 배치 발송 중 오류 발생: {}", capsuleId, e.getMessage());
            }
        }

        log.info("배치 발송 완료. 처리된 캡슐 수: {}", capsuleIds.size());
    }
}