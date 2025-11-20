package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {

    private final BaseEmailService baseEmailService;

    @Async("emailTaskExecutor")
    public void send(EmailPayload payload) {
        log.info("[ASYNC] 스레드={}, 이메일 전송 시작: to={}",
                Thread.currentThread().getName(), payload.to());
        baseEmailService.send(payload);
        log.info("[ASYNC] 스레드={}, 이메일 전송 완료: to={}",
                Thread.currentThread().getName(), payload.to());
    }
}