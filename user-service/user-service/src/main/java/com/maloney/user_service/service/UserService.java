package com.maloney.user_service.service;


import com.maloney.user_service.dto.*;
import com.maloney.user_service.entity.Role;
import com.maloney.user_service.entity.User;
import com.maloney.user_service.exception.DuplicateResourceException;
import com.maloney.user_service.exception.ResourceNotFoundException;
import com.maloney.user_service.feign.WalletServiceClient;
import com.maloney.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletServiceClient walletServiceClient;

    // Create user (called by auth-service via Feign)
    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User already exists with email: "
                    + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        User saved = userRepository.save(user);

        // Call wallet-service via Feign
        WalletServiceClient.CreateWalletRequest walletRequest =
                new WalletServiceClient.CreateWalletRequest();
        walletRequest.setUserId(saved.getId());
        walletRequest.setCurrency("NGN");
        walletServiceClient.createWallet(walletRequest);
        return mapToResponse(saved);
    }

    // Get user by ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponse(user);
    }

    //  Get user by email
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DuplicateResourceException("User not found with email: " + email));
        return mapToResponse(user);
    }

    // Update user
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        User updated = userRepository.save(user);
        return mapToResponse(updated);
    }

    // Map entity to response DTO
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}