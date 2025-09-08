package com.example.timecapsule_backend.controller.email;

import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestRequest;
import com.example.timecapsule_backend.controller.email.dto.EmailPerformanceTestResponse;
import com.example.timecapsule_backend.service.email.EmailServiceFacade;
import com.example.timecapsule_backend.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 발송 API")
public class EmailController {

    private final EmailServiceFacade emailServiceFacade;

    // ================== 단건 발송 API ==================
    @PostMapping("/send/sync")
    @Operation(summary = "동기 이메일 발송", description = "이메일을 동기 방식으로 발송합니다.")
    public ResponseEntity<ApiResult<String>> sendEmailSync(@Valid @RequestBody EmailRequest emailRequest) {
        emailServiceFacade.sendSyncEmail(emailRequest);
        return ResponseEntity.ok(ApiResult.success("이메일이 동기 방식으로 성공적으로 발송되었습니다."));
    }
    
    @PostMapping("/send/async")
    @Operation(summary = "비동기 이메일 발송", description = "이메일을 비동기 방식으로 발송합니다.")
    public ResponseEntity<ApiResult<String>> sendEmailAsync(@Valid @RequestBody EmailRequest emailRequest) {
        emailServiceFacade.sendAsyncEmail(emailRequest);
        return ResponseEntity.ok(ApiResult.success("이메일이 비동기 방식으로 성공적으로 발송되었습니다."));
    }

    @PostMapping("/send/cf")
    @Operation(summary = "CompletableFuture 이메일 발송", description = "CompletableFuture 방식으로 이메일을 발송합니다.")
    public ResponseEntity<ApiResult<String>> sendEmailCompletableFuture(@Valid @RequestBody EmailRequest emailRequest) {
        emailServiceFacade.sendCfEmail(emailRequest);
        return ResponseEntity.ok(ApiResult.success("CompletableFuture 방식으로 이메일이 성공적으로 발송되었습니다."));
    }

    @PostMapping("/send/redis-queue")
    @Operation(summary = "Redis Queue 이메일 발송", description = "Redis Queue 방식으로 이메일을 발송합니다.")
    public ResponseEntity<ApiResult<String>> sendEmailRedisQueue(@Valid @RequestBody EmailRequest emailRequest) {
        emailServiceFacade.sendRedisQueueEmail(emailRequest);
        return ResponseEntity.ok(ApiResult.success("Redis Queue 방식으로 이메일이 큐에 추가되었습니다."));
    }

    // ================== 성능 테스트 API ==================
    @PostMapping("/performance-test/sync")
    @Operation(summary = "동기 이메일 성능 테스트", description = "동기 방식으로 대량 이메일 발송 성능을 테스트합니다.")
    public ResponseEntity<ApiResult<EmailPerformanceTestResponse>> testSyncPerformance(@Valid @RequestBody EmailPerformanceTestRequest request) {
        EmailPerformanceTestResponse response = emailServiceFacade.performSyncTest(request);
        return ResponseEntity.ok(ApiResult.success(response));
    }
    
    @PostMapping("/performance-test/async")
    @Operation(summary = "비동기 이메일 성능 테스트", description = "비동기 방식으로 대량 이메일 발송 성능을 테스트합니다.")
    public ResponseEntity<ApiResult<EmailPerformanceTestResponse>> testAsyncPerformance(@Valid @RequestBody EmailPerformanceTestRequest request) {
        EmailPerformanceTestResponse response = emailServiceFacade.performAsyncTest(request);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @PostMapping("/performance-test/cf")
    @Operation(summary = "CompletableFuture 이메일 성능 테스트", description = "CompletableFuture 방식으로 대량 이메일 발송 성능을 테스트합니다.")
    public ResponseEntity<ApiResult<EmailPerformanceTestResponse>> testCfPerformance(@Valid @RequestBody EmailPerformanceTestRequest request) {
        EmailPerformanceTestResponse response = emailServiceFacade.performCfTest(request);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @PostMapping("/performance-test/redis-queue")
    @Operation(summary = "Redis Queue 이메일 성능 테스트", description = "Redis Queue 방식으로 대량 이메일 발송 성능을 테스트합니다.")
    public ResponseEntity<ApiResult<EmailPerformanceTestResponse>> testRedisQueuePerformance(@Valid @RequestBody EmailPerformanceTestRequest request) {
        EmailPerformanceTestResponse response = emailServiceFacade.performRedisQueueTest(request);
        return ResponseEntity.ok(ApiResult.success(response));
    }
}
