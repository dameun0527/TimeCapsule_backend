package com.example.timecapsule_backend.controller.capsule.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttachmentRequest {

    @NotBlank
    private String originalFilename;

    @NotBlank
    private String storedUrl;
}
