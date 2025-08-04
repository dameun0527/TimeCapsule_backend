package com.example.timecapsule_backend.controller.capsule.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CapsuleContentRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String alias;

    @NotBlank
    private String mainMessage;
}
