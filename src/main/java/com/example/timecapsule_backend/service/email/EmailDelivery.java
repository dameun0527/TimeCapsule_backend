package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;

public interface EmailDelivery {
    void send(EmailPayload payload);
}