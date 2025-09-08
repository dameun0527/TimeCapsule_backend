package com.example.timecapsule_backend.controller.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 성능 테스트 결과")
public class EmailPerformanceTestResponse {
    
    @Schema(description = "테스트 시작 시간")
    private LocalDateTime startTime;
    
    @Schema(description = "테스트 종료 시간")
    private LocalDateTime endTime;
    
    @Schema(description = "총 소요 시간 (밀리초)")
    private Long totalDurationMs;
    
    @Schema(description = "테스트한 이메일 개수")
    private Integer emailCount;
    
    @Schema(description = "평균 처리 시간 (밀리초)")
    private Double averageDurationMs;
    
    @Schema(description = "초당 처리량 (TPS)")
    private Double throughputPerSecond;
    
    @Schema(description = "테스트 타입", example = "SYNC 또는 ASYNC")
    private String testType;
    
    @Schema(description = "성공 여부")
    private Boolean success;
    
    @Schema(description = "에러 메시지")
    private String errorMessage;
}