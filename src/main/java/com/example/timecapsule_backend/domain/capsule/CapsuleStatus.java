package com.example.timecapsule_backend.domain.capsule;

public enum CapsuleStatus {
    PENDING,
    SCHEDULED,
    DELIVERED,
    PARTIALLY_DELIVERED,  // 일부 수신자만 성공, 일부 최종 실패
    FAILED,
    CANCELLED;
}
