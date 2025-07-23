package com.example.timecapsule_backend.service;

import com.example.timecapsule_backend.config.security.loginUser.dto.LoginRequest;
import com.example.timecapsule_backend.config.security.loginUser.dto.LoginResponse;
import com.example.timecapsule_backend.controller.user.dto.request.JoinRequest;
import com.example.timecapsule_backend.controller.user.dto.response.JoinResponse;
import com.example.timecapsule_backend.domain.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    /**
     * 회원 가입
     * @param request 가입 요청 정보
     * @return 가입 결과
     */
    @Transactional
    public JoinResponse join(JoinRequest request) {
        // 중복 사용자 확인
        // 비밀번호 암호화
        // 사용자 생성 및 저장
        // 응답 반환
        return null;
    }

    /**
     * 로그인
     * @param request 로그인 요청 정보
     * @return JWT 토큰 및 사용자 정보
     */
    public LoginResponse login(LoginRequest request) {
        // 인증 수행
        // JWT 토큰 생성
        // 사용자 정보 조회
        // 응답 반환
        return null;
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * @return 현재 사용자 정보
     */
    public User getCurrentUser() {
        // SecurityContext에서 인증 정보 조회
        // 사용자 정보 반환
        return null;
    }

    /**
     * 사용자 존재 여부 확인
     * @param username 사용자명
     * @return 존재 여부
     */
    public boolean existsByUsername(String username) {
        // 사용자명으로 존재 여부 확인
        return false;
    }

    /**
     * 사용자 존재 여부 확인 (이메일)
     * @param email 이메일
     * @return 존재 여부
     */
    public boolean existsByEmail(String email) {
        // 이메일로 존재 여부 확인
        return false;
    }
}
