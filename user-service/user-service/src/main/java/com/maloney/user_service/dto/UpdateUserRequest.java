package com.maloney.user_service.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String phone;
}