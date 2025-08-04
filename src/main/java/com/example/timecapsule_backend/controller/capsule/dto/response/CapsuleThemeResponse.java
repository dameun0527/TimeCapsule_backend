package com.example.timecapsule_backend.controller.capsule.dto.response;

import com.example.timecapsule_backend.domain.capsule.CapsuleTheme;
import com.example.timecapsule_backend.domain.capsule.ThemeType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CapsuleThemeResponse {

    private final Long id;
    private final ThemeType themeType;
    private final String themeMetadata;

    public static CapsuleThemeResponse from(CapsuleTheme capsuleTheme) {
        return CapsuleThemeResponse.builder()
                .id(capsuleTheme.getId())
                .themeType(capsuleTheme.getThemeType())
                .themeMetadata(capsuleTheme.getThemeMetadata())
                .build();
    }
}
