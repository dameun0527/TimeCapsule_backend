package com.example.timecapsule_backend.service.delivery;

import com.example.timecapsule_backend.config.scheduler.CapsuleSchedulerConfig;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleRecipient;
import com.example.timecapsule_backend.domain.capsule.CapsuleRepository;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLog;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLogRepository;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import com.example.timecapsule_backend.service.email.EmailServiceFacade;
import com.example.timecapsule_backend.service.email.EmailMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final CapsuleRepository capsuleRepository;
    private final DeliveryLogRepository deliveryLogRepository;
    private final EmailServiceFacade emailServiceFacade;
    private final CapsuleSchedulerConfig schedulerConfig;

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

        // PENDING 상태인 수신자만 필터링 (이미 성공한 수신자는 제외)
        List<CapsuleRecipient> pendingRecipients = capsule.getPendingRecipients();

        if (pendingRecipients.isEmpty()) {
            log.info("캡슐 {} 발송할 대상 수신자 없음", capsuleId);
            capsule.updateStatusBasedOnRecipients(schedulerConfig.getBaseBackoffSeconds());
            return;
        }

        log.info("캡슐 {} 발송 시작. 대상 수신자 수: {}", capsuleId, pendingRecipients.size());

        // 각 수신자별로 개별 발송 시도
        for (CapsuleRecipient recipient : pendingRecipients) {
            try {
                EmailRequest emailRequest = new EmailRequest(
                        recipient.getUser().getEmail(),
                        capsule.getContent().getTitle(),
                        capsule.getContent().getMainMessage(),
                        EmailRequest.EmailType.TIMECAPSULE,
                        capsule.getTheme() != null ? capsule.getTheme().getThemeType() : null
                );

                // 개별 이메일 발송
                emailServiceFacade.sendSyncEmail(emailRequest);

                // 발송 성공
                recipient.markDelivered();
                log.info("캡슐 {} - 수신자 {} ({}) 발송 성공",
                        capsuleId, recipient.getUser().getEmail(), recipient.getId());

            } catch (Exception e) {
                // 발송 실패
                recipient.markFailedAttempt(e.getMessage(), schedulerConfig.getMaxRetries());
                log.warn("캡슐 {} - 수신자 {} ({}) 발송 실패: {}. retryCount={}",
                        capsuleId, recipient.getUser().getEmail(), recipient.getId(),
                        e.getMessage(), recipient.getRetryCount());
            }
        }

        // 수신자들의 발송 상태를 기반으로 캡슐 상태 업데이트
        capsule.updateStatusBasedOnRecipients(schedulerConfig.getBaseBackoffSeconds());

        // DeliveryLog 저장
        deliveryLogRepository.save(
                DeliveryLog.builder()
                        .capsule(capsule)
                        .capsuleStatus(capsule.getStatus())
                        .attemptedAt(LocalDateTime.now())
                        .build());

        log.info("캡슐 {} 발송 완료. 최종 상태: {}", capsuleId, capsule.getStatus());
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