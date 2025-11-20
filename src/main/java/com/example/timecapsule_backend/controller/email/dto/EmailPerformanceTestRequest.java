package com.example.timecapsule_backend.controller.email.dto;

import com.example.timecapsule_backend.domain.capsule.ThemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "이메일 성능 테스트 요청")
public class EmailPerformanceTestRequest {
    
    @Schema(description = "테스트할 이메일 개수", example = "10")
    @NotNull
    @Min(1)
    @Max(100)
    private Integer emailCount;
    
    @Schema(description = "수신자 이메일", example = "test@example.com")
    @NotNull
    private String to;
    
    @Schema(description = "이메일 제목", example = "성능 테스트")
    @NotNull
    private String subject;
    
    @Schema(description = "이메일 내용", example = "테스트 메시지")
    @NotNull
    private String content;
    
    @Schema(description = "이메일 타입", example = "TEXT")
    @NotNull
    private EmailRequest.EmailType emailType;

    @Schema(description = "타임캡슐 테마", example = "CHRISTMAS_TREE")
    private ThemeType themeType;
}