package com.example.timecapsule_backend.controller.user.dto.response;

import com.example.timecapsule_backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserUpdateResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDate birthDate;
    private String phoneNumber;

    public static UserUpdateResponse fromEntity(User user) {
        return UserUpdateResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
