package com.example.timecapsule_backend.controller.capsule.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CapsuleCreateRequest {

    @NotNull
    private LocalDateTime scheduledAt;

    @NotNull
    private CapsuleContentRequest content;

    @NotNull
    private CapsuleThemeRequest theme;

    private List<Long> recipients;

    private List<AttachmentRequest> attachments;


}
