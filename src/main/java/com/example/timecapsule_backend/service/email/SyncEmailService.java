package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncEmailService {

    private final BaseEmailService baseEmailService;

    public void send(EmailPayload payload) {
        baseEmailService.send(payload);
    }
}