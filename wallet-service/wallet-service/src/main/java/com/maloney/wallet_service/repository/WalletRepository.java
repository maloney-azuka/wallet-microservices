package com.maloney.wallet_service.repository;

import com.maloney.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(Long userId);
    Optional<Wallet> findByAccountNumber(String accountNumber);
    boolean existsByUserId(Long userId);
}