package com.example.timecapsule_backend.controller.capsule;

import com.example.timecapsule_backend.config.security.loginUser.LoginUser;
import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleCreateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.request.CapsuleUpdateRequest;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleResponse;
import com.example.timecapsule_backend.controller.capsule.dto.response.CapsuleSummaryResponse;
import com.example.timecapsule_backend.controller.delivery.dto.DeliveryLogResponse;
import com.example.timecapsule_backend.service.capsule.CapsuleService;
import com.example.timecapsule_backend.service.delivery.DeliveryService;
import com.example.timecapsule_backend.util.api.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Capsule", description = "타임캡슐 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/capsules")
public class CapsuleController {

    private final CapsuleService capsuleService;
    private final DeliveryService deliveryService;

    @Operation(summary = "타임캡슐 생성", description = "새로운 타임캡슐을 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "타임캡슐 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<ApiResult<CapsuleResponse>> createCapsule(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody CapsuleCreateRequest request) {
        Long userId = loginUser.getUser().getId();
        CapsuleResponse response = capsuleService.createCapsule(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResult.success(response));
    }

    @Operation(summary = "타임캡슐 목록 조회", description = "현재 사용자의 타임캡슐 목록을 페이징으로 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResult<Page<CapsuleSummaryResponse>>> listCapsules(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "페이징 정보 (기본 size=20)") @PageableDefault(size = 20) Pageable pageable) {
        Long userId = loginUser.getUser().getId();
        Page<CapsuleSummaryResponse> response = capsuleService.listCapsules(userId, pageable);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @Operation(summary = "타임캡슐 상세 조회", description = "특정 타임캡슐의 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음")
    })
    @GetMapping("/{capsuleId}")
    public ResponseEntity<ApiResult<CapsuleResponse>> getCapsule(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "타임캡슐 ID") @PathVariable Long capsuleId) {
        Long userId = loginUser.getUser().getId();
        CapsuleResponse response = capsuleService.getCapsule(userId, capsuleId);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @Operation(summary = "타임캡슐 수정", description = "타임캡슐 정보를 수정합니다. (발송 전만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 수정 불가능한 상태"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음")
    })
    @PutMapping("/{capsuleId}")
    public ResponseEntity<ApiResult<CapsuleResponse>> updateCapsule(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "타임캡슐 ID") @PathVariable Long capsuleId,
            @Valid @RequestBody CapsuleUpdateRequest request) {
        Long userId = loginUser.getUser().getId();
        CapsuleResponse response = capsuleService.updateCapsule(userId, capsuleId, request);
        return ResponseEntity.ok(ApiResult.success(response));
    }

    @Operation(summary = "타임캡슐 삭제", description = "타임캡슐을 삭제(취소)합니다. (발송 전만 가능)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "삭제 불가능한 상태"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음")
    })
    @DeleteMapping("/{capsuleId}")
    public ResponseEntity<ApiResult<Void>> deleteCapsule(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "타임캡슐 ID") @PathVariable Long capsuleId) {
        Long userId = loginUser.getUser().getId();
        capsuleService.deleteCapsule(userId, capsuleId);
        return ResponseEntity.ok(ApiResult.success(null));
    }

    @Operation(summary = "타임캡슐 발송 이력 조회", description = "특정 타임캡슐의 발송 이력을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "타임캡슐을 찾을 수 없음")
    })
    @GetMapping("/{capsuleId}/logs")
    public ResponseEntity<ApiResult<List<DeliveryLogResponse>>> getDeliveryLogs(
            @Parameter(hidden = true) @AuthenticationPrincipal LoginUser loginUser,
            @Parameter(description = "타임캡슐 ID") @PathVariable Long capsuleId) {
        Long userId = loginUser.getUser().getId();
        List<DeliveryLogResponse> response = deliveryService.getDeliveryLogs(userId, capsuleId);
        return ResponseEntity.ok(ApiResult.success(response));
    }
}
