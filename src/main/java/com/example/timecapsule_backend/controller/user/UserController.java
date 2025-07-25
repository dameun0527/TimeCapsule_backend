package com.example.timecapsule_backend.controller.user;


import com.example.timecapsule_backend.config.security.loginUser.LoginUser;
import com.example.timecapsule_backend.controller.user.dto.request.SignupRequestDto;
import com.example.timecapsule_backend.controller.user.dto.request.UserUpdateRequest;
import com.example.timecapsule_backend.controller.user.dto.response.UserResponse;
import com.example.timecapsule_backend.controller.user.dto.response.UserUpdateResponse;
import com.example.timecapsule_backend.service.UserService;
import com.example.timecapsule_backend.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResult> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        String message = userService.signup(requestDto);
        return ResponseEntity.ok(ApiResult.success(message));
    }

    @GetMapping("/users/mypage")
    public ResponseEntity<ApiResult<UserResponse>> getMyPage(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getUser().getId();
        return ResponseEntity.ok(ApiResult.success(userService.getMyPage(userId)));
    }

    @PatchMapping("/users/mypage")
    public ResponseEntity<ApiResult<UserUpdateResponse>> updateMyPage(
            @AuthenticationPrincipal LoginUser loginUser,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
            ) {
        Long userId = loginUser.getUser().getId();
        UserUpdateResponse response = userService.updateMyPage(userId, userUpdateRequest);
        return ResponseEntity.ok(ApiResult.success(response));
    }
}
