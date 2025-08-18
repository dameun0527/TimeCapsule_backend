package com.example.timecapsule_backend.controller.email;

import com.example.timecapsule_backend.controller.email.dto.EmailRequest;
import com.example.timecapsule_backend.service.email.EmailService;
import com.example.timecapsule_backend.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Tag(name = "Email", description = "이메일 발송 API")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    @Operation(summary = "수동 이메일 전송", description = "텍스트, HTML, 타임캡슐 테마 이메일을 수동으로 전송합니다.")
    public ResponseEntity<ApiResult<String>> sendEmail(@Valid @RequestBody EmailRequest emailRequest) {
        emailService.sendEmail(emailRequest);
        return ResponseEntity.ok(ApiResult.success("이메일이 성공적으로 전송되었습니다."));
    }
}
