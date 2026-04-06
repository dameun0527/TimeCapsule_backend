package com.example.timecapsule_backend.domain.capsule;

public enum RecipientDeliveryStatus {
    PENDING,    // 발송 대기 또는 재시도 예정
    DELIVERED,  // 발송 성공
    FAILED      // 최종 실패 (3회 재시도 후)
}
