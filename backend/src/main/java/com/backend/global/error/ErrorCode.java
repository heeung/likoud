package com.backend.global.error;

import org.springframework.http.HttpStatus;


public enum ErrorCode {

    TEST(HttpStatus.INTERNAL_SERVER_ERROR, "001", "business exception test"),

    // 인증 && 인가
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Auth-001", "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Auth-002", "해당 refresh token은 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Auth-003", "해당 refresh token이 존재하지 않습니다."),
    NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "Auth-004", "해당 토큰은 ACCESS TOKEN이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Auth-005", "해당 토큰은 유효한 토큰이 아닙니다."),
    NOT_EXISTS_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "Auth-006", "Authorization Header가 없습니다."),
    NOT_MATCH_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "Auth-007", "인증 타입이 Bearer 타입이 아닙니다."),
    FORBIDDEN_ADMIN(HttpStatus.FORBIDDEN, "Auth-008", "관리자가 아닙니다."),

    // 회원
    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "User-001", "잘못된 회원 타입 입니다."),
    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "User-002", "이미 가입된 회원 입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST, "User-003", "이미 등록된 번호입니다."),
    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "User-004", "해당 회원은 존재하지 않습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "User-005", "해당 아이디는 이미 존재합니다."),
    UNAVAILABLE_LOGIN_ID(HttpStatus.BAD_REQUEST, "User-006", "사용할 수 없는 아이디입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "User-007", "인증에 실패하였습니다."),
    ROLE_NOT_EXISTS(HttpStatus.FORBIDDEN, "User-008", "해당 권한이 없습니다."),
    TOKEN_NOT_EXISTS(HttpStatus.NOT_FOUND, "User-009", "해당 key의 인증 토큰이 존재하지 않는 경우"),

    FILE_UPLOAD_CONFLICT(HttpStatus.CONFLICT, "COMMON-002", "파일 업로드에 실패하였습니다."),
    ;

    private HttpStatus httpStatus;
    private String errorCode;
    private String description;

    ErrorCode(HttpStatus httpStatus, String errorCode, String description) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.description = description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}
