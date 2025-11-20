package com.example.timecapsule_backend.service.email;

public enum EmailMode {
    SYNC,         // 동기식 - 안전하지만 느림
    ASYNC,        // Spring @Async - 균형잡힌 성능
    CF,           // CompletableFuture - 고급 비동기 제어
    REDIS_QUEUE   // Redis Queue - 메시지 큐 기반 처리
}
