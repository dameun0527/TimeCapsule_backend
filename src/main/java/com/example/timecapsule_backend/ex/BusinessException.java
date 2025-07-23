package com.example.timecapsule_backend.ex;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    @NotNull
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
