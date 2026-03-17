package com.maloney.user_service.controller;

import com.maloney.user_service.dto.*;
import com.maloney.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.ok(
                ApiResponse.success("User created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully", response));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", response));
    }
}