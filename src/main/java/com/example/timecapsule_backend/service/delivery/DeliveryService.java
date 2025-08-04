package com.example.timecapsule_backend.service.delivery;

import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryService {

    /**
     * 발송 대상 캡슐 ID 목록 조회 (스케줄러용)
     * @param now 현재 시간
     * @return 발송 대상 캡슐 ID 목록
     */
    List<Long> findDueCapsuleIds(LocalDateTime now);

    /**
     * 캡슐 발송 처리
     * @param capsuleId 캡슐 ID
     */
    void dispatch(Long capsuleId);

    /**
     * 캡슐 상태 변경
     * @param capsuleId 캡슐 ID
     * @param status 변경할 상태
     */
    void changeStatus(Long capsuleId, CapsuleStatus status);

    /**
     * 캡슐 발송 이력 조회
     * @param userId 사용자 ID
     * @param capsuleId 캡슐 ID
     * @return 발송 이력 목록
     */
    List<DeliveryLogResponse> getDeliveryLogs(Long userId, Long capsuleId);

    /**
     * 배치 발송 처리
     * @param capsuleIds 발송할 캡슐 ID 목록
     */
    void processBatchDelivery(List<Long> capsuleIds);
}