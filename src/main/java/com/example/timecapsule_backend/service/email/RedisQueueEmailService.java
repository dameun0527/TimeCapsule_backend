package com.example.timecapsule_backend.service.email;

import com.example.timecapsule_backend.controller.email.dto.EmailPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisQueueEmailService implements EmailDelivery {

    private static final String EMAIL_QUEUE_KEY = "email:queue";
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void send(EmailPayload payload) {
        try {
            // EmailPayload를 JSON으로 직렬화해서 Redis Queue에 추가
            String jsonPayload = objectMapper.writeValueAsString(payload);
            redisTemplate.opsForList().leftPush(EMAIL_QUEUE_KEY, jsonPayload);
            
            log.info("이메일 작업을 Redis 큐에 추가했습니다. 수신자: {}", payload.to());
        } catch (JsonProcessingException e) {
            log.error("이메일 페이로드 직렬화 실패", e);
            throw new RuntimeException("Redis 큐 추가 실패", e);
        }
    }
}