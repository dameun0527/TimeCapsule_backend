package com.example.timecapsule_backend.controller.user;


import com.example.timecapsule_backend.controller.user.dto.request.SignupRequestDto;
import com.example.timecapsule_backend.service.UserService;
import com.example.timecapsule_backend.util.api.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

}
