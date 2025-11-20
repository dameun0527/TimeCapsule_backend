package com.example.timecapsule_backend.controller.email.dto;

import com.example.timecapsule_backend.domain.capsule.ThemeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
        @NotBlank(message = "수신자 이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String to,

        @NotBlank(message = "제목은 필수입니다.")

        String subject,

        @NotBlank(message = "내용은 필수입니다.")
        String content,


        EmailType emailType,

        ThemeType themeType
) {
    public enum EmailType {
        TEXT, HTML, TIMECAPSULE
    }
}
