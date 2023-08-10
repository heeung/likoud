package com.backend.api.logout.controller;

import com.backend.api.logout.service.LogoutService;
import com.backend.global.util.AuthorizationHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "Auth", description = "소셜로그인 관련 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class LogoutController {

    private final LogoutService logoutService;

    // 로그아웃
    @Operation(summary = "로그아웃", description = "로그아웃 관련 메서드입니다.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest) {
        // 헤더 검증
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        AuthorizationHeaderUtils.validateAuthorization(authorizationHeader);

        String accessToken = authorizationHeader.split(" ")[1];
        logoutService.logout(accessToken);

        return ResponseEntity.ok("logout success");
    }
}
