package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CompletableFutureEmailService {

    private final BaseEmailService baseEmailService;
    private final Executor executor;

    public CompletableFutureEmailService(BaseEmailService baseEmailService,
                                         @Qualifier("emailTaskExecutor") Executor executor) {
        this.baseEmailService = baseEmailService;
        this.executor = executor;
    }

    public void send(EmailPayload payload) {
        CompletableFuture
                .runAsync(() -> baseEmailService.send(payload), executor)
                .orTimeout(30, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("[CF] 이메일 전송 실패: to={}, error={}", payload.to(), ex.getMessage());
                    return null;
                });
    }
}
