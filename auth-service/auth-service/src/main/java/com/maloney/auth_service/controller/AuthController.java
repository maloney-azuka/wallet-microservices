package com.maloney.auth_service.controller;

import com.maloney.auth_service.dto.ApiResponse;
import com.maloney.auth_service.dto.AuthResponse;
import com.maloney.auth_service.dto.LoginRequest;
import com.maloney.auth_service.dto.RegisterRequest;
import com.maloney.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(
                ApiResponse.success("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestHeader("Authorization") String authHeader) {
        AuthResponse response = authService.refreshToken(authHeader);
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        return ResponseEntity.ok(
                ApiResponse.success("Logged out successfully"));
    }
}