package com.maloney.wallet_service.service;

import com.maloney.wallet_service.dto.*;
import com.maloney.wallet_service.entity.Wallet;
import com.maloney.wallet_service.entity.WalletStatus;
import com.maloney.wallet_service.exception.BadRequestException;
import com.maloney.wallet_service.exception.DuplicateResourceException;
import com.maloney.wallet_service.exception.ResourceNotFoundException;
import com.maloney.wallet_service.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    //  Create wallet
    public WalletResponse createWallet(CreateWalletRequest request) {

        if (walletRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException("Wallet already exists for user: "
                    + request.getUserId());
        }

        String accountNumber = accountNumberGenerator.generate();
        while (walletRepository.findByAccountNumber(accountNumber).isPresent()) {
            accountNumber = accountNumberGenerator.generate();
        }

        Wallet wallet = Wallet.builder()
                .userId(request.getUserId())
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency() != null
                        ? request.getCurrency() : "NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        Wallet saved = walletRepository.save(wallet);
        return mapToResponse(saved);
    }

    // Get wallet by user ID
    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user: " + userId));
        return mapToResponse(wallet);
    }

    // Get balance
    public BigDecimal getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user: " + userId));
        return wallet.getBalance();
    }

    //  Credit wallet
    @Transactional
    public WalletResponse credit(CreditDebitRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user: " + request.getUserId()));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new RuntimeException("Wallet is not active");
        }

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);
        return mapToResponse(updated);
    }

    //Debit wallet
    @Transactional
    public WalletResponse debit(CreditDebitRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user: " + request.getUserId()));

        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new RuntimeException("Wallet is not active");
        }

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        Wallet updated = walletRepository.save(wallet);
        return mapToResponse(updated);
    }

    // Map to response
    private WalletResponse mapToResponse(Wallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .accountNumber(wallet.getAccountNumber())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}