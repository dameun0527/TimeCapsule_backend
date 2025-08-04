package com.example.timecapsule_backend.controller.capsule.dto.request;

import com.example.timecapsule_backend.domain.capsule.ThemeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CapsuleThemeRequest {

    @NotNull
    private ThemeType themeType;

    @NotBlank
    private String themeMetadata;
}
