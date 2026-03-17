package com.maloney.wallet_service.controller;

import com.maloney.wallet_service.dto.*;
import com.maloney.wallet_service.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @Valid @RequestBody CreateWalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ResponseEntity.ok(
                ApiResponse.success("Wallet created successfully", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(
            @PathVariable Long userId) {
        WalletResponse response = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Wallet retrieved successfully", response));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
            @PathVariable Long userId) {
        BigDecimal balance = walletService.getBalance(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Balance retrieved successfully", balance));
    }

    @PutMapping("/credit")
    public ResponseEntity<ApiResponse<WalletResponse>> credit(
            @Valid @RequestBody CreditDebitRequest request) {
        WalletResponse response = walletService.credit(request);
        return ResponseEntity.ok(
                ApiResponse.success("Wallet credited successfully", response));
    }

    @PutMapping("/debit")
    public ResponseEntity<ApiResponse<WalletResponse>> debit(
            @Valid @RequestBody CreditDebitRequest request) {
        WalletResponse response = walletService.debit(request);
        return ResponseEntity.ok(
                ApiResponse.success("Wallet debited successfully", response));
    }
}