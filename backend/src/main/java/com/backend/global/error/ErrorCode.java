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
    FORBIDDEN_MEMBER(HttpStatus.FORBIDDEN, "Auth-009", "회원이 아닙니다. 추가정보를 입력해 주세요."),
    UNAUTHORIZED_MEMBER(HttpStatus.UNAUTHORIZED, "Auth-010", "본인이 작성한 게시물만 수정/삭제할 수 있습니다."),

    // 회원
    INVALID_SOCIAL_TYPE(HttpStatus.BAD_REQUEST, "User-001", "잘못된 로그인 타입 입니다."),
    ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST, "User-002", "이미 가입된 회원 입니다."),
    ALREADY_REGISTERED_NICKNAME(HttpStatus.BAD_REQUEST, "User-003", "이미 등록된 닉네임입니다."),
    MEMBER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "User-004", "해당 회원은 존재하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "User-005", "인증에 실패하였습니다."),
    ROLE_NOT_EXISTS(HttpStatus.FORBIDDEN, "User-006", "해당 권한이 없습니다."),
    NOT_ENOUGH_COIN(HttpStatus.BAD_REQUEST, "User-007", "보유 코인이 부족합니다."),

    // 그림
    NOT_FOUND_DRAWING(HttpStatus.NOT_FOUND, "Drawing-001", "그림을 찾을 수 없습니다."),

    // 사진
    NOT_FOUND_PHOTO(HttpStatus.NOT_FOUND, "Photo-001", "사진을 찾을 수 없습니다."),
    INVALID_PHOTO(HttpStatus.BAD_REQUEST, "Photo-002", "적합하지 않은 사진입니다."),

    // NFT
    NOT_FOUND_WALLET(HttpStatus.NOT_FOUND, "NFT-001", "지갑을 찾을 수 없습니다."),
    ALREADY_PUBLISHED_TOKEN(HttpStatus.BAD_REQUEST, "NFT-002", "이미 발행된 토큰입니다."),
    UNAUTHORIZED_NFT(HttpStatus.UNAUTHORIZED, "NFT-003", "본인이 그린 그림만 NFT 발행할 수 있습니다."),
    NOT_FOUND_NFT(HttpStatus.NOT_FOUND, "NFT-004", "NFT를 찾을 수 없습니다."),
    ALREADY_WALLET(HttpStatus.BAD_REQUEST, "NFT-005", "이미 지갑이 있습니다."),
    UNAUTHORIZED_KEY(HttpStatus.UNAUTHORIZED, "NFT-006", "토큰과 사용자의 인증키가 다릅니다."),
    NOT_FOUND_TRANSFER(HttpStatus.NOT_FOUND, "NFT-007", "해당 거래내역을 찾을 수 없습니다."),
    CANNOT_GIFT_TO_SELF(HttpStatus.BAD_REQUEST, "NFT-008", "본인에게는 선물할 수 없습니다."),


    // 아이템
    NOT_FOUND_ACCESSORY(HttpStatus.NOT_FOUND, "Item-001", "악세사리를 찾을 수 없습니다."),
    ALREADY_PURCHASED_ITEM(HttpStatus.BAD_REQUEST, "Item-002", "이미 구매한 아이템입니다."),

    // 댓글
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "Comment-001", "댓글을 찾을 수 없습니다."),

    // 파일
    FILE_UPLOAD_CONFLICT(HttpStatus.CONFLICT, "File-001", "파일 업로드에 실패하였습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "File-002", " 파일 이름이 올바르지 않습니다."),
    NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "File-003", "파일을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String description;

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
