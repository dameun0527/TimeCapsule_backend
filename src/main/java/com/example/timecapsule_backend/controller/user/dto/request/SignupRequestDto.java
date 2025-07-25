package com.example.timecapsule_backend.controller.user.dto.request;

import com.example.timecapsule_backend.controller.user.dto.valid.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 1, message = "이름은 최소 1자 이상 입력해야 합니다.")
    private String username;

    @Email
    @NotBlank(message = "이메일을 입력하세요.")
    private String email;

    @ValidPassword
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$",
            message = "비밀번호는 영문자와 숫자 조합으로 최소 8글자, 최대 12글자로 입력해주세요."
    )
    private String password;

    @NotNull(message = "생년월일을 입력하세요.")
    private LocalDate birthDate;

    @NotBlank(message = "휴대폰 번호를 입력하세요.")
    @Pattern(regexp = "^(010)[0-9]{7,8}$", message = "휴대폰 번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

}
