package com.maloney.auth_service.service;

import com.maloney.auth_service.dto.AuthResponse;
import com.maloney.auth_service.dto.LoginRequest;
import com.maloney.auth_service.dto.RegisterRequest;
import com.maloney.auth_service.entity.Token;
import com.maloney.auth_service.entity.User;
import com.maloney.auth_service.entity.Role;
import com.maloney.auth_service.exception.DuplicateResourceException;
import com.maloney.auth_service.feign.UserServiceClient;
import com.maloney.auth_service.repository.TokenRepository;
import com.maloney.auth_service.repository.UserRepository;
import com.maloney.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserServiceClient userServiceClient;

    //Register
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        UserServiceClient.CreateUserRequest userRequest =
                new UserServiceClient.CreateUserRequest();
        userRequest.setFullName(savedUser.getFullName());
        userRequest.setEmail(savedUser.getEmail());
        userRequest.setPhone(savedUser.getPhone());
        userServiceClient.createUser(userRequest);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedUser.getEmail())
                .password(savedUser.getPassword())
                .authorities("ROLE_" + savedUser.getRole().name())
                .build();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        saveUserToken(savedUser, accessToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    // Login
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // Refresh Token
    public AuthResponse refreshToken(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid refresh token");
        }

        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token expired");
        }

        String newAccessToken = jwtService.generateToken(userDetails);

        revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    // Logout
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return;

        String jwt = authHeader.substring(7);
        Token token = tokenRepository.findByToken(jwt).orElse(null);

        if (token != null) {
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.save(token);
        }
    }

    // Helpers
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .revoked(false)
                .expired(false)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validTokens =
                tokenRepository.findAllValidTokensByUser(user.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validTokens);
    }
}