package com.example.timecapsule_backend.controller.capsule.dto.response;

import com.example.timecapsule_backend.domain.capsule.Capsule;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class CapsuleResponse {

    private final Long id;
    private final CapsuleContentResponse content;
    private final CapsuleThemeResponse theme;
    private final List<Long> recipientIds;
    private final List<AttachmentResponse> attachments;
    private final CapsuleStatus status;
    private final LocalDateTime scheduledAt;
    private final LocalDateTime deliveredAt;

    public static CapsuleResponse from(Capsule capsule) {
        return CapsuleResponse.builder()
                .id(capsule.getId())
                .content(CapsuleContentResponse.from(capsule.getContent()))
                .theme(CapsuleThemeResponse.from(capsule.getTheme()))
                .recipientIds(capsule.getRecipients().stream()
                        .map(r -> r.getUser().getId())
                        .collect(Collectors.toList())
                )
                .attachments(capsule.getAttachments().stream()
                        .map(AttachmentResponse::from)
                        .collect(Collectors.toList())
                )
                .status(capsule.getStatus())
                .scheduledAt(capsule.getScheduledAt())
                .deliveredAt(capsule.getDeliveredAt())
                .build();
    }
}
