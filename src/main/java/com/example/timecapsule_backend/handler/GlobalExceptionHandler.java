package com.example.timecapsule_backend.handler;

import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.util.api.ApiError;
import com.example.timecapsule_backend.util.api.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<ApiError>> handleParseError(HttpMessageNotReadableException e) {
        log.warn("JSON 파싱 오류: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ApiResult.error(400, "잘못된 요청 형식입니다. JSON 구조를 확인하세요."));
    }


    // 400: @RequestBody 유효성 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        err -> err.getField(),
                        err -> err.getDefaultMessage(),
                        (msg1, msg2) -> msg1 + ", " + msg2
                ));
        log.warn("입력값 유효성 검증 실패: {}", errors);
        return ResponseEntity
                .badRequest()
                .body(ApiResult.error(400, "입력값이 유효하지 않습니다.", errors));
    }


    // 400: 파라미터 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResult<ApiError>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String msg = String.format("파라미터 '%s'의 타입이 올바르지 않습니다. 기대 타입: %s",
                e.getName(), e.getRequiredType().getSimpleName());
        log.warn("타입 불일치: {}", msg);
        return ResponseEntity
                .badRequest()
                .body(ApiResult.error(400, msg));
    }


    // 커스텀 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<ApiError>> handleBusinessException(BusinessException e) {
        int status = e.getErrorCode().getStatus();
        String message = e.getErrorCode().getMessage();
        log.warn("상태 = {}, 메시지 = {}", status, message);
        return ResponseEntity
                .status(status)
                .body(ApiResult.error(status, message));
    }


    // 500: 그 외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<ApiError>> handleAll(Exception e) {
        log.error("예기치 못한 오류 발생", e);
        return ResponseEntity
                .internalServerError()
                .body(ApiResult.error(500, e.getMessage()));
    }
}
