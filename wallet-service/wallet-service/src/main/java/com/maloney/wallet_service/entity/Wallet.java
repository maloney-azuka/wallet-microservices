package com.maloney.wallet_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    private WalletStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.currency == null) this.currency = "NGN";
        if (this.status == null) this.status = WalletStatus.ACTIVE;
    }
}