package com.example.timecapsule_backend.service;

import com.example.timecapsule_backend.controller.user.dto.request.SignupRequestDto;
import com.example.timecapsule_backend.controller.user.dto.request.UserUpdateRequest;
import com.example.timecapsule_backend.controller.user.dto.response.UserResponse;
import com.example.timecapsule_backend.controller.user.dto.response.UserUpdateResponse;
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

import java.time.LocalDate;

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
        LocalDate birthDate = requestDto.getBirthDate();
        String phoneNumber = requestDto.getPhoneNumber();

        // 이메일 중복 확인
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_EMAIL_ALREADY_EXISTS);
        }

        Role role = Role.USER;
        User user = new User(userName, email, password, birthDate, phoneNumber, role);
        userRepository.save(user);
        return "회원 가입 성공";

    }

    // 마이페이지 조회
    public UserResponse getMyPage(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.fromEntity(user);
    }

    // 마이페이지 수정
    @Transactional
    public UserUpdateResponse updateMyPage(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updateMyPage(request.getUsername(), request.getBirthDate(), request.getPhoneNumber());
        return UserUpdateResponse.fromEntity(user);
    }

}
