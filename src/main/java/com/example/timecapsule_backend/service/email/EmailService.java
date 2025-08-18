package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import com.example.timecapsule_backend.domain.capsule.ThemeType;

public interface EmailService {
    void sendTextEmail(String to, String subject, String content);
    void sendHtmlEmail(String to, String subject, String htmlContent);
    void sendTimeCapsuleEmail(String to, String capsuleTitle, String content, ThemeType themeType);
    void sendEmail(EmailRequest emailRequest);
}
