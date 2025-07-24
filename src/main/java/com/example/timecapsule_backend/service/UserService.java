package com.example.timecapsule_backend.service;

import com.example.timecapsule_backend.controller.user.dto.request.SignupRequestDto;
import com.example.timecapsule_backend.domain.user.Role;
import com.example.timecapsule_backend.domain.user.User;
import com.example.timecapsule_backend.domain.user.UserRepository;
import com.example.timecapsule_backend.ex.BusinessException;
import com.example.timecapsule_backend.ex.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     *
     * @param requestDto 가입 요청 정보
     * @return 가입 결과
     */
    @Transactional
    public String signup(SignupRequestDto requestDto) {

        String userName = requestDto.getUsername();
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        Role role = Role.USER;
        User user = new User(userName, email, password, role);
        userRepository.save(user);
        return "회원 가입 성공";
    }

}
