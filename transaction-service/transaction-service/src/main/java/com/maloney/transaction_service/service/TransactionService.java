package com.maloney.transaction_service.service;

import com.maloney.transaction_service.dto.*;
import com.maloney.transaction_service.entity.*;
import com.maloney.transaction_service.exception.BadRequestException;
import com.maloney.transaction_service.exception.ResourceNotFoundException;
import com.maloney.transaction_service.feign.WalletServiceClient;
import com.maloney.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletServiceClient walletServiceClient;
    private final ReferenceGenerator referenceGenerator;

    // Deposit
    @Transactional
    public TransactionResponse deposit(DepositRequest request) {

        Transaction transaction = Transaction.builder()
                .senderId(request.getUserId())
                .amount(request.getAmount())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PENDING)
                .reference(referenceGenerator.generate())
                .description(request.getDescription())
                .build();

        transactionRepository.save(transaction);

        try {
            WalletServiceClient.CreditDebitRequest creditRequest =
                    new WalletServiceClient.CreditDebitRequest();
            creditRequest.setUserId(request.getUserId());
            creditRequest.setAmount(request.getAmount());
            walletServiceClient.credit(creditRequest);

            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    // Withdraw
    @Transactional
    public TransactionResponse withdraw(WithdrawRequest request) {

        Transaction transaction = Transaction.builder()
                .senderId(request.getUserId())
                .amount(request.getAmount())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PENDING)
                .reference(referenceGenerator.generate())
                .description(request.getDescription())
                .build();

        transactionRepository.save(transaction);

        try {
            WalletServiceClient.CreditDebitRequest debitRequest =
                    new WalletServiceClient.CreditDebitRequest();
            debitRequest.setUserId(request.getUserId());
            debitRequest.setAmount(request.getAmount());
            walletServiceClient.debit(debitRequest);

            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    // Transfer
    @Transactional
    public TransactionResponse transfer(TransferRequest request) {

        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new BadRequestException("Cannot transfer to same wallet");
        }

        Transaction transaction = Transaction.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .reference(referenceGenerator.generate())
                .description(request.getDescription())
                .build();

        transactionRepository.save(transaction);

        try {
            // Debit sender
            WalletServiceClient.CreditDebitRequest debitRequest =
                    new WalletServiceClient.CreditDebitRequest();
            debitRequest.setUserId(request.getSenderId());
            debitRequest.setAmount(request.getAmount());
            walletServiceClient.debit(debitRequest);

            // Credit receiver
            WalletServiceClient.CreditDebitRequest creditRequest =
                    new WalletServiceClient.CreditDebitRequest();
            creditRequest.setUserId(request.getReceiverId());
            creditRequest.setAmount(request.getAmount());
            walletServiceClient.credit(creditRequest);

            transaction.setStatus(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    // Get transaction by ID
    public TransactionResponse getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found: " + id));
        return mapToResponse(transaction);
    }

    // Get all transactions for a user
    public List<TransactionResponse> getByUserId(Long userId) {
        List<Transaction> sent = transactionRepository
                .findBySenderIdOrderByCreatedAtDesc(userId);
        List<Transaction> received = transactionRepository
                .findByReceiverIdOrderByCreatedAtDesc(userId);

        sent.addAll(received);
        return sent.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Map to response
    private TransactionResponse mapToResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .senderId(t.getSenderId())
                .receiverId(t.getReceiverId())
                .amount(t.getAmount())
                .type(t.getType())
                .status(t.getStatus())
                .reference(t.getReference())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
