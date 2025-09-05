package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisEmailWorker {

    private static final String EMAIL_QUEUE_KEY = "email:queue";
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BaseEmailService baseEmailService;

    @Scheduled(fixedDelay = 1000) // 1초마다 큐 확인
    public void processEmailQueue() {
        try {
            // Redis Queue에서 하나씩 꺼내서 처리
            String jsonPayload = (String) redisTemplate.opsForList().rightPop(EMAIL_QUEUE_KEY);
            
            if (jsonPayload != null) {
                EmailPayload payload = objectMapper.readValue(jsonPayload, EmailPayload.class);
                
                // 실제 이메일 발송
                baseEmailService.send(payload);
                log.info("Redis 큐에서 이메일 발송 완료. 수신자: {}", payload.to());
            }
        } catch (JsonProcessingException e) {
            log.error("이메일 페이로드 역직렬화 실패", e);
        } catch (Exception e) {
            log.error("Redis 큐 이메일 처리 중 오류 발생", e);
            // TODO: 실패한 작업을 DLQ(Dead Letter Queue)로 이동
        }
    }
    
    // 큐 상태 모니터링을 위한 메서드
    public long getQueueSize() {
        Long size = redisTemplate.opsForList().size(EMAIL_QUEUE_KEY);
        return size != null ? size : 0;
    }
}