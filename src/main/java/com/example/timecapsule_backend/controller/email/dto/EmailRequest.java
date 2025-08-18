package com.example.timecapsule_backend.controller.email.dto;

import com.example.timecapsule_backend.domain.capsule.ThemeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequest {

    @NotBlank(message = "수신자 이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String to;

    @NotBlank(message = "제목은 필수입니다.")
    private String subject;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private EmailType emailType = EmailType.TEXT;
    
    private ThemeType themeType;

    public enum EmailType {
        TEXT, HTML, TIMECAPSULE
    }
}
