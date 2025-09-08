package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestRequest;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestResponse;
import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;


@Service
@RequiredArgsConstructor
public class EmailServiceFacade {

    private final SyncEmailService syncEmailService;
    private final AsyncEmailService asyncEmailService;
    private final CompletableFutureEmailService completableFutureEmailService;
    private final RedisQueueEmailService redisQueueEmailService;


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

    // ================== 대량 전송(배치/수동 공용) ==================
    public void sendBulkEmails(List<EmailRequest> requests, EmailMode mode) {
        Consumer<EmailPayload> sender = pick(mode);
        for (EmailRequest request : requests) {
            sender.accept(toPayload(request));
        }
    }

    // ================== 성능 테스트 ==================
    public EmailPerformanceTestResponse performSyncTest(EmailPerformanceTestRequest request) {
        return bulk(request, "SYNC", pick(EmailMode.SYNC));
    }

    public EmailPerformanceTestResponse performAsyncTest(EmailPerformanceTestRequest request) {
        return bulk(request, "ASYNC", pick(EmailMode.ASYNC));
    }

    public EmailPerformanceTestResponse performCfTest(EmailPerformanceTestRequest request) {
        return bulk(request, "CF", pick(EmailMode.CF));
    }

    public EmailPerformanceTestResponse performRedisQueueTest(EmailPerformanceTestRequest request) {
        return bulk(request, "REDIS_QUEUE", pick(EmailMode.REDIS_QUEUE));
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
        EmailRequest.EmailType type = (emailRequest.emailType() == null)
                ? EmailRequest.EmailType.TEXT : emailRequest.emailType();
        return new EmailPayload(
                emailRequest.to(),
                emailRequest.subject(),
                type == EmailRequest.EmailType.TEXT ? emailRequest.content() : null,
                (type == EmailRequest.EmailType.HTML || type == EmailRequest.EmailType.TIMECAPSULE) ? emailRequest.content() : null,
                emailRequest.themeType(),
                null
        );
    }

    private EmailPerformanceTestResponse bulk(EmailPerformanceTestRequest request,
                                              String label,
                                              Consumer<EmailPayload> sender) {
        long t0 = System.nanoTime();
        LocalDateTime start = LocalDateTime.now();
        int count = request.getEmailCount();

        try {
            for (int i = 0; i < count; i++) {
                EmailRequest emailRequest = new EmailRequest(
                        request.getTo(),
                        request.getSubject() + " #" + (i + 1),
                        request.getContent() + " (테스트 #" + (i + 1) + ")",
                        request.getEmailType(),
                        request.getThemeType()
                );
                sender.accept(toPayload(emailRequest));
            }
            long totalMs = Duration.ofNanos(System.nanoTime() - t0).toMillis();
            double tps = count == 0 ? 0 : (count * 1000.0 / Math.max(totalMs, 1));

            return EmailPerformanceTestResponse.builder()
                    .startTime(start)
                    .endTime(LocalDateTime.now())
                    .totalDurationMs(totalMs)
                    .emailCount(count)
                    .averageDurationMs(count == 0 ? 0 : totalMs * 1.0 / count)
                    .throughputPerSecond(tps)
                    .testType(label)
                    .success(true)
                    .build();
        } catch (Exception e) {
            long totalMs = Duration.ofNanos(System.nanoTime() - t0).toMillis();
            return EmailPerformanceTestResponse.builder()
                    .startTime(start)
                    .endTime(LocalDateTime.now())
                    .totalDurationMs(totalMs)
                    .emailCount(count)
                    .testType(label)
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}