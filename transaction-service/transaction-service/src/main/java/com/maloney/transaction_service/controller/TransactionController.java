package com.maloney.transaction_service.controller;

import com.maloney.transaction_service.dto.*;
import com.maloney.transaction_service.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody DepositRequest request) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.ok(
                ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody WithdrawRequest request) {
        TransactionResponse response = transactionService.withdraw(request);
        return ResponseEntity.ok(
                ApiResponse.success("Withdrawal successful", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.ok(
                ApiResponse.success("Transfer successful", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(
            @PathVariable Long id) {
        TransactionResponse response = transactionService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Transaction retrieved successfully", response));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getByUserId(
            @PathVariable Long userId) {
        List<TransactionResponse> response = transactionService.getByUserId(userId);
        return ResponseEntity.ok(
                ApiResponse.success("Transactions retrieved successfully", response));
    }
}