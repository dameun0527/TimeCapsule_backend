package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisEmailWorker {

    private static final String EMAIL_QUEUE_KEY = "email:queue";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BaseEmailService baseEmailService;
    private final DeliveryService deliveryService;

    public RedisEmailWorker(RedisTemplate<String, Object> redisTemplate,
                            ObjectMapper objectMapper,
                            BaseEmailService baseEmailService,
                            @Lazy DeliveryService deliveryService) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.baseEmailService = baseEmailService;
        this.deliveryService = deliveryService;
    }

    @Scheduled(fixedDelay = 1000)
    public void processEmailQueue() {
        String jsonPayload = null;
        try {
            jsonPayload = (String) redisTemplate.opsForList().rightPop(EMAIL_QUEUE_KEY);
            if (jsonPayload == null) return;

            EmailPayload payload = objectMapper.readValue(jsonPayload, EmailPayload.class);
            baseEmailService.send(payload);
            log.info("Redis 큐에서 이메일 발송 완료. 수신자: {}", payload.to());

            if (payload.recipientId() != null) {
                deliveryService.markRecipientDelivered(payload.recipientId());
            }

        } catch (JsonProcessingException e) {
            log.error("이메일 페이로드 역직렬화 실패", e);
            // 역직렬화 실패는 재시도해도 동일하게 실패하므로 DLQ로 이동 필요
        } catch (Exception e) {
            log.error("Redis 큐 이메일 처리 중 오류 발생. 수신자 실패 처리", e);
            if (jsonPayload != null) {
                tryMarkFailed(jsonPayload, e.getMessage());
            }
        }
    }
    
    private void tryMarkFailed(String jsonPayload, String reason) {
        try {
            EmailPayload payload = objectMapper.readValue(jsonPayload, EmailPayload.class);
            if (payload.recipientId() != null) {
                deliveryService.markRecipientFailed(payload.recipientId(), reason);
            }
        } catch (Exception ex) {
            log.error("수신자 실패 처리 중 오류 발생", ex);
        }
    }

    public long getQueueSize() {
        Long size = redisTemplate.opsForList().size(EMAIL_QUEUE_KEY);
        return size != null ? size : 0;
    }
}