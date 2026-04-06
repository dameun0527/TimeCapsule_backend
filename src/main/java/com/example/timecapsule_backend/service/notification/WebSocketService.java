package com.example.timecapsule_backend.service.notification;

import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    // TODO: SimpMessagingTemplate 의존성 주입

    public void sendCapsuleDeliveredEvent(Long userId, Long capsuleId, String message) {
        try {
            // TODO: WebSocket으로 실시간 이벤트 전송
        } catch (Exception e) {
            // TODO: 예외 로깅
        }
    }
}