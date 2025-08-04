package com.example.timecapsule_backend.controller.capsule.dto.request;

import com.example.timecapsule_backend.domain.capsule.ThemeType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CapsuleUpdateRequest {

    private String title;
    private String alias;
    private String mainMessage;
    private LocalDateTime scheduledAt;
    private ThemeType themeType;
    private String themeMetadata;
    private List<Long> recipients;
    private List<AttachmentRequest> attachments;
}
