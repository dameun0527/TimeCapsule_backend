package com.example.timecapsule_backend.controller.capsule.dto.response;

import com.example.timecapsule_backend.domain.capsule.Attachment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttachmentResponse {

    private final Long id;
    private final String originalFilename;
    private final String storeUrl;

    public static AttachmentResponse from(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalFilename(attachment.getOriginalFilename())
                .storeUrl(attachment.getStoredUrl())
                .build();
    }
}
