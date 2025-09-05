package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncEmailService {

    private final BaseEmailService baseEmailService;

    @Async("emailTaskExecutor")
    public void send(EmailPayload payload) {
        baseEmailService.send(payload);
    }
}