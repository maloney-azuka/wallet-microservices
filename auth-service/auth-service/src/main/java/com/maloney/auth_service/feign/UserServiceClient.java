package com.maloney.auth_service.feign;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/api/users")
    UserResponse createUser(@RequestBody CreateUserRequest request);

    @Data
    class CreateUserRequest {
        private String fullName;
        private String email;
        private String phone;
    }

    @Data
    class UserResponse {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
        private String role;
    }
}