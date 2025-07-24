package com.example.timecapsule_backend.config.security.handler;

import com.example.timecapsule_backend.util.api.ApiError;
import com.example.timecapsule_backend.util.api.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class SecurityResponseHandler {

    private final ObjectMapper objectMapper;

    public void fail(HttpServletResponse response, String msg, HttpStatus httpStatus) {
        try {
            ApiResult<ApiError> error = ApiResult.error(httpStatus.value(), msg);
            String responseBody = objectMapper.writeValueAsString(error);

            response.setContentType("application/json; charset=utf-8");
            response.setStatus(httpStatus.value());
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("시큐리티 예외 응답 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }


    public void success(HttpServletResponse response, Object object) {
        try {
            String responseBody = objectMapper.writeValueAsString(ApiResult.success(object));
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("시큐리티 성공 응답 생성 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
