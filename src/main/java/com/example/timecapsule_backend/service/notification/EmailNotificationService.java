package com.example.timecapsule_backend.service.notification;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    // TODO: JavaMailSender 의존성 주입

    public boolean sendEmail(String email, String subject, String content) {
        try {
            // TODO: 이메일 전송 구현
            return true;
        } catch (Exception e) {
            // TODO: 예외 로깅
            return false;
        }
    }

    public boolean sendCapsuleDeliveredEmail(String userEmail, String userName, String capsuleTitle) {
        String subject = "🕰️ 타임캡슐이 도착했습니다!";
        String content = String.format("안녕하세요 %s님, '%s' 캡슐이 도착했습니다!", userName, capsuleTitle);
        return sendEmail(userEmail, subject, content);
    }
}