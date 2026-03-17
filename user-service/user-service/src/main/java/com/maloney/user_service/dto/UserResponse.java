package com.maloney.user_service.dto;

import com.maloney.user_service.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
}