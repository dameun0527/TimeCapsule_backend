package com.example.timecapsule_backend.controller.email.dto;

import com.example.timecapsule_backend.domain.capsule.ThemeType;

public record EmailPayload (
    String to,
    String subject,
    String text,
    String html,
    ThemeType theme,
    String capsuleTitle
){
}
