package com.example.timecapsule_backend.controller.delivery;

import com.example.timecapsule_backend.config.security.loginUser.LoginUser;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.domain.capsule.CapsuleStatus;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import com.example.timecapsule_backend.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Delivery", description = "타임캡슐 발송 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "발송 대상 캡슐 조회", description = "현재 시간 기준으로 발송해야 할 캡슐 ID 목록을 조회합니다. (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/due")
    public ResponseEntity<ApiResult<List<Long>>> getDueCapsules() {
        List<Long> dueCapsuleIds = deliveryService.findDueCapsuleIds(LocalDateTime.now());
        return ResponseEntity.ok(ApiResult.success(dueCapsuleIds));
    }

    @Operation(summary = "캡슐 수동 발송", description = "특정 캡슐을 수동으로 발송합니다. (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "발송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "캡슐을 찾을 수 없음")
    })
    @PostMapping("/dispatch/{capsuleId}")
    public ResponseEntity<ApiResult<String>> dispatchCapsule(
            @Parameter(description = "캡슐 ID") @PathVariable Long capsuleId) {
        deliveryService.dispatch(capsuleId);
        return ResponseEntity.ok(ApiResult.success("캡슐 발송이 완료되었습니다."));
    }

    @Operation(summary = "배치 발송", description = "발송 대상 캡슐들을 일괄 발송합니다. (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배치 발송 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/batch")
    public ResponseEntity<ApiResult<String>> processBatchDelivery() {
        List<Long> dueCapsuleIds = deliveryService.findDueCapsuleIds(LocalDateTime.now());
        deliveryService.processBatchDelivery(dueCapsuleIds);
        return ResponseEntity.ok(ApiResult.success(
                String.format("배치 발송이 완료되었습니다. 처리된 캡슐 수: %d", dueCapsuleIds.size())
        ));
    }

    @Operation(summary = "캡슐 상태 변경", description = "캡슐의 상태를 변경합니다. (관리자용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "캡슐을 찾을 수 없음")
    })
    @PatchMapping("/status/{capsuleId}")
    public ResponseEntity<ApiResult<String>> changeCapsuleStatus(
            @Parameter(description = "캡슐 ID") @PathVariable Long capsuleId,
            @Parameter(description = "변경할 상태") @RequestParam CapsuleStatus status) {
        deliveryService.changeStatus(capsuleId, status);
        return ResponseEntity.ok(ApiResult.success("캡슐 상태가 변경되었습니다."));
    }

    @Operation(summary = "발송 이력 조회", description = "특정 캡슐의 발송 이력을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "캡슐을 찾을 수 없음")
    })
    @GetMapping("/logs/{capsuleId}")
    public ResponseEntity<ApiResult<List<DeliveryLogResponse>>> getDeliveryLogs(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "캡슐 ID") @PathVariable Long capsuleId) {
        Long userId = loginUser.getUser().getId();
        List<DeliveryLogResponse> response = deliveryService.getDeliveryLogs(userId, capsuleId);
        return ResponseEntity.ok(ApiResult.success(response));
    }
}
