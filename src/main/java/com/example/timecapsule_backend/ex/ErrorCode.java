package com.example.timecapsule_backend.ex;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 사용자 관련
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.", 404),
    USER_ALREADY_EXISTS("이미 존재하는 사용자입니다.", 409),
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다.", 400),
    USER_EMAIL_ALREADY_EXISTS("이미 사용중인 Email입니다.",409),
    
    // 캡슐 관련
    CAPSULE_NOT_FOUND("캡슐을 찾을 수 없습니다.", 404),
    CAPSULE_ALREADY_DELIVERED("이미 발송된 캡슐입니다.", 400),
    INVALID_DELIVERY_TIME("발송 시간이 유효하지 않습니다.", 400),
    CAPSULE_ACCESS_DENIED("캡슐에 접근할 권한이 없습니다.", 403),
    SCHEDULE_CREATION_NOT_ALLOWED("이 캡슐은 예약할 수 없는 상태입니다.",409),
    SCHEDULE_UPDATE_NOT_ALLOWED("예약된 캡슐만 시간 변경이 가능합니다.", 409),
    DELIVER_NOT_ALLOWED("예약된 캡슐만 발송 요청이 가능합니다.", 409),
    CANCEL_NOT_ALLOWED("대기 중이거나 예약된 캡슐만 취소할 수 있습니다.", 409),
    // 서버 관련
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다.", 500),
    DATABASE_ERROR("데이터베이스 오류가 발생했습니다.", 500),
    
    // 유효성 검사
    INVALID_INPUT("입력값이 유효하지 않습니다.", 400),
    MISSING_REQUIRED_FIELD("필수 필드가 누락되었습니다.", 400);

    private final String message;
    private final int status;

    ErrorCode(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
