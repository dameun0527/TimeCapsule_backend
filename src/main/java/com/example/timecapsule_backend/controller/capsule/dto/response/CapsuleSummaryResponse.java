package com.example.timecapsule_backend.controller.capsule.dto.response;

import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleContent;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CapsuleSummaryResponse {

    private final Long id;
    private final String title;
    private final CapsuleStatus status;
    private final LocalDateTime scheduledAt;

    public static CapsuleSummaryResponse from(Capsule capsule) {
        CapsuleContent content = capsule.getContent();
        return CapsuleSummaryResponse.builder()
                .id(capsule.getId())
                .title(content.getTitle())
                .status(capsule.getStatus())
                .scheduledAt(capsule.getScheduledAt())
                .build();
    }
}
