package com.example.timecapsule_backend.service.capsule;

import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleCreateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleUpdateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleResponse;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleSummaryResponse;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.*;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLogRepository;
import com.example.timecapsule_backend.domain.user.User;
import com.example.timecapsule_backend.domain.user.UserRepository;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;
    private final DeliveryLogRepository deliveryLogRepository;


    /**
     * 타임캡슐 생성
     * @param request 생성 요청 정보
     * @param userId 사용자 ID
     * @return 생성된 캡슐 정보
     */
    @Override
    @Transactional
    public  CapsuleResponse createCapsule(Long userId, CapsuleCreateRequest request) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Capsule capsule = Capsule.of(sender);
        capsule.schedule(request.getScheduledAt());
        capsule.addContent(CapsuleContent.of(
                capsule,
                request.getContent().getTitle(),
                request.getContent().getAlias(),
                request.getContent().getMainMessage()
        ));
        capsule.addTheme(CapsuleTheme.of(
                capsule,
                request.getTheme().getThemeType(),
                request.getTheme().getThemeMetadata()
        ));

        if (request.getRecipients() != null) {
            for (Long rid : request.getRecipients()) {
                User receiver = userRepository.findById(rid)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                capsule.addRecipient(CapsuleRecipient.of(capsule, receiver));
            }
        }

        if (request.getAttachments() != null) {
            for (var attachment : request.getAttachments()) {
                capsule.addAttachment(Attachment.of(
                        capsule,
                        attachment.getOriginalFilename(),
                        attachment.getStoredUrl()
                ));
            }
        }

        Capsule saved = capsuleRepository.save(capsule);
        return CapsuleResponse.from(saved);

    }

    /**
     * 내 캡슐 목록 조회 (페이징)
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 캡슐 목록
     */
    @Override
    public Page<CapsuleSummaryResponse> listCapsules(Long userId, Pageable pageable) {
        return capsuleRepository.findByUserId(userId, pageable)
                .map(CapsuleSummaryResponse::from);
    }


    /**
     * 캡슐 상세 조회
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     * @return 캡슐 상세 정보
     */
    @Override
    public CapsuleResponse getCapsule(Long userId, Long capsuleId) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));
        return CapsuleResponse.from(capsule);
    }


    /**
     * 캡슐 수정 (발송 전만 가능)
     * @param capsuleId 캡슐 ID
     * @param request 수정 요청 정보
     * @param userId 사용자 ID
     * @return 수정된 캡슐 정보
     */
    @Override
    @Transactional
    public CapsuleResponse updateCapsule(Long userId, Long capsuleId, CapsuleUpdateRequest request) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));
        if (request.getTitle() != null
        || request.getAlias() != null
        || request.getMainMessage() != null) {
            capsule.getContent().update(
                    request.getTitle(),
                    request.getAlias(),
                    request.getMainMessage()
            );
        }

        if (request.getScheduledAt() != null) {
            capsule.updateSchedule(request.getScheduledAt());
        }

        if (request.getThemeType() != null || request.getThemeMetadata() != null) {
            capsule.getTheme().update(
                    request.getThemeType(),
                    request.getThemeMetadata()
            );
        }

        if (request.getRecipients() != null) {
            capsule.clearRecipients();
            for (Long rid : request.getRecipients()) {
                User receiver = userRepository.findById(rid)
                        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
                capsule.addRecipient(CapsuleRecipient.of(capsule, receiver));
            }
        }

        if (request.getAttachments() != null) {
            capsule.clearAttachments();
            for (var attachment : request.getAttachments()) {
                capsule.addAttachment(Attachment.of(
                        capsule,
                        attachment.getOriginalFilename(),
                        attachment.getStoredUrl()
                ));
            }
        }
        return CapsuleResponse.from(capsule);
    }


    /**
     * 캡슐 삭제 (발송 전만 가능)
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     */
    @Override
    @Transactional
    public void deleteCapsule(Long userId, Long capsuleId) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));
        capsule.cancel();
    }


    /**
     * 발송 대상 캡슐 조회 (스케줄러용)
     * @return 발송 대상 캡슐 목록
     */
    @Override
    public List<Long> findDueCapsuleIds(LocalDateTime now) {
        return capsuleRepository
                .findByStatusAndScheduledAtBefore(CapsuleStatus.SCHEDULED, now)
                .stream()
                .map(Capsule::getId)
                .toList();
    }


    /**
     * 캡슐 상태 변경
     * @param capsuleId 캡슐 ID
     * @param status 변경할 상태
     */
    @Override
    @Transactional
    public void changeStatus(Long capsuleId, CapsuleStatus status) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(capsuleId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));
        switch (status) {
            case CANCELLED -> capsule.cancel();
            case DELIVERED -> capsule.markDelivered();
            default -> throw new BusinessException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
    }


    /**
     * 캡슐 발송 이력 조회
     * @param capsuleId 캡슐 ID
     * @param userId 사용자 ID
     * @return 발송 이력 목록
     */
    @Override
    public List<DeliveryLogResponse> getDeliveryLogs(Long userId, Long capsuleId) {
        capsuleRepository.findById(capsuleId)
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new BusinessException(ErrorCode.CAPSULE_NOT_FOUND));

        return deliveryLogRepository.findByCapsuleId(capsuleId).stream()
                .map(DeliveryLogResponse::from)
                .toList();
    }
}
