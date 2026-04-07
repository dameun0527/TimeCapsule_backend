package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.config.email.EmailDeliveryConfig;
import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EmailServiceFacade {

    private final SyncEmailService syncEmailService;
    private final AsyncEmailService asyncEmailService;
    private final CompletableFutureEmailService completableFutureEmailService;
    private final RedisQueueEmailService redisQueueEmailService;
    private final EmailDeliveryConfig emailDeliveryConfig;

    // ================== 수동 단건 전송 ==================
    public void sendSyncEmail(EmailRequest emailRequest) {
        syncEmailService.send(toPayload(emailRequest));
    }

    public void sendAsyncEmail(EmailRequest emailRequest) {
        asyncEmailService.send(toPayload(emailRequest));
    }

    public void sendCfEmail(EmailRequest emailRequest) {
        completableFutureEmailService.send(toPayload(emailRequest));
    }

    public void sendRedisQueueEmail(EmailRequest emailRequest) {
        redisQueueEmailService.send(toPayload(emailRequest));
    }

    // ================== 기본 전략으로 단건 전송 ==================
    public void sendByDefaultStrategy(EmailRequest emailRequest) {
        pick(emailDeliveryConfig.getDefaultStrategy()).accept(toPayload(emailRequest));
    }

    // 스케줄러 발송용 - recipientId 포함
    public void sendByDefaultStrategy(EmailRequest emailRequest, Long recipientId) {
        pick(emailDeliveryConfig.getDefaultStrategy()).accept(toPayload(emailRequest, recipientId));
    }

    // ================== 내부 공통 ==================
    private Consumer<EmailPayload> pick(EmailMode mode) {
        return switch (mode) {
            case SYNC -> syncEmailService::send;
            case ASYNC -> asyncEmailService::send;
            case CF -> completableFutureEmailService::send;
            case REDIS_QUEUE -> redisQueueEmailService::send;
        };
    }

    private EmailPayload toPayload(EmailRequest emailRequest) {
        return toPayload(emailRequest, null);
    }

    private EmailPayload toPayload(EmailRequest emailRequest, Long recipientId) {
        EmailRequest.EmailType type = (emailRequest.emailType() == null)
                ? EmailRequest.EmailType.TEXT : emailRequest.emailType();
        return new EmailPayload(
                emailRequest.to(),
                emailRequest.subject(),
                type == EmailRequest.EmailType.TEXT ? emailRequest.content() : null,
                (type == EmailRequest.EmailType.HTML || type == EmailRequest.EmailType.TIMECAPSULE) ? emailRequest.content() : null,
                emailRequest.themeType(),
                null,
                recipientId
        );
    }
}
