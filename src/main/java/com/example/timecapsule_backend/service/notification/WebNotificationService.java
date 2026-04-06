package com.example.timecapsule_backend.service.notification;

import org.springframework.stereotype.Service;

@Service
public class WebNotificationService {

    public boolean sendWebNotification(Long userId, String title, String body, Object data) {
        try {
            // TODO: Web Push API를 통한 브라우저 알림 전송
            return true;
        } catch (Exception e) {
            // TODO: 예외 로깅
            return false;
        }
    }

    public boolean sendCapsuleDeliveredNotification(Long userId, String capsuleTitle, Long capsuleId) {
        String title = "🕰️ 타임캡슐 도착!";
        String body = String.format("'%s' 캡슐이 도착했습니다.", capsuleTitle);
        return sendWebNotification(userId, title, body, capsuleId);
    }
}