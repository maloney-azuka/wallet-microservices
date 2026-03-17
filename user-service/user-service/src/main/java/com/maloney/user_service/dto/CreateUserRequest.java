package com.maloney.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    private String phone;
}