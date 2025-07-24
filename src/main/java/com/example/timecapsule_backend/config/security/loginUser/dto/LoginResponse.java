package com.example.timecapsule_backend.config.security.loginUser.dto;

import com.example.timecapsule_backend.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResponse {

    public static final String SUCCESS = "로그인 성공";
    private Long id;
    private String message;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.message = SUCCESS;
    }
}
