package com.example.timecapsule_backend.controller.delivery.dto;

import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.domain.deliveryLog.DeliveryLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryLogResponse {

    private Long id;
    private Long capsuleId;
    private CapsuleStatus capsuleStatus;
    private LocalDateTime attemptedAt;

    public static DeliveryLogResponse from(DeliveryLog deliveryLog) {
        return DeliveryLogResponse.builder()
                .id(deliveryLog.getId())
                .capsuleId(deliveryLog.getCapsule().getId())
                .capsuleStatus(deliveryLog.getCapsuleStatus())
                .attemptedAt(deliveryLog.getAttemptedAt())
                .build();
    }
}
