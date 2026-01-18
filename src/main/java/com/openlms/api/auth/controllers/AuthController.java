package com.openlms.api.auth.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openlms.api.auth.dtos.requests.LoginRequest;
import com.openlms.api.auth.dtos.requests.SignUpRequest;
import com.openlms.api.auth.dtos.requests.VerifyOtpRequest;
import com.openlms.api.auth.dtos.responses.AuthResponse;
import com.openlms.api.auth.services.AuthService;
import com.openlms.api.commons.apis.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ApiResponse<Void> signup(
        @RequestBody @Valid SignUpRequest request
    ) {
        authService.signUp(request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/send-otp")
    public ApiResponse<Void> sendOtp(
        @RequestParam String email
    ) {
        System.out.println(email);
        authService.sendOtp(email);
        return ApiResponse.ok(null);
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOtp(
        @RequestBody @Valid VerifyOtpRequest request
    ) {
        authService.verifyOtp(request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
        @RequestBody @Valid LoginRequest request
    ) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<Object> getSelf(
        @AuthenticationPrincipal Jwt jwt
    ) {
        return ApiResponse.ok(jwt.getClaims());
    }
}
