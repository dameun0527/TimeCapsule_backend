package com.example.timecapsule_backend.service.monitoring;

import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    // TODO: MeterRegistry 의존성 주입

    public void incrementApiRequest(String endpoint, String method, int status) {
        // TODO: API 요청 카운터
    }

    public void incrementCapsuleDelivery(boolean success) {
        // TODO: 캡슐 발송 메트릭
    }
}