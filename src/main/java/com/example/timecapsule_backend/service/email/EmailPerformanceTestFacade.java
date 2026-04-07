package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.config.email.PerformanceTestConfig;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestRequest;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestResponse;
import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class EmailPerformanceTestFacade {

    private final EmailServiceFacade emailServiceFacade;
    private final MeterRegistry meterRegistry;
    private final PerformanceTestConfig performanceTestConfig;

    public EmailPerformanceTestResponse performSyncTest(EmailPerformanceTestRequest request) {
        return bulk(request, "SYNC", emailServiceFacade::sendSyncEmail);
    }

    public EmailPerformanceTestResponse performAsyncTest(EmailPerformanceTestRequest request) {
        return bulk(request, "ASYNC", emailServiceFacade::sendAsyncEmail);
    }

    public EmailPerformanceTestResponse performCfTest(EmailPerformanceTestRequest request) {
        return bulk(request, "CF", emailServiceFacade::sendCfEmail);
    }

    public EmailPerformanceTestResponse performRedisQueueTest(EmailPerformanceTestRequest request) {
        return bulk(request, "REDIS_QUEUE", emailServiceFacade::sendRedisQueueEmail);
    }

    private EmailPerformanceTestResponse bulk(EmailPerformanceTestRequest request,
                                              String label,
                                              Consumer<EmailRequest> sender) {
        int count = request.getEmailCount();
        if (count > performanceTestConfig.getMaxEmailCount()) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL_COUNT);
        }

        long t0 = System.nanoTime();
        LocalDateTime start = LocalDateTime.now();

        Timer timer = Timer.builder("email.performance.test")
                .tag("strategy", label)
                .register(meterRegistry);
        try {
            for (int i = 0; i < count; i++) {
                EmailRequest emailRequest = new EmailRequest(
                        request.getTo(),
                        request.getSubject() + " #" + (i + 1),
                        request.getContent() + " (테스트 #" + (i + 1) + ")",
                        request.getEmailType(),
                        request.getThemeType()
                );
                timer.record(() -> sender.accept(emailRequest));
                meterRegistry.counter("email.test.count", "strategy", label, "status", "success").increment();
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
