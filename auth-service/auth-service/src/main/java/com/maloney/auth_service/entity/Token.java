package com.maloney.auth_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 500)
    private String token;

    private boolean revoked;

    private boolean expired;

    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}