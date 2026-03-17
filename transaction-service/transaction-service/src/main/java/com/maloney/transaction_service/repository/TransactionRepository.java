package com.maloney.transaction_service.repository;


import com.maloney.transaction_service.entity.Transaction;
import com.maloney.transaction_service.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    List<Transaction> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Transaction> findBySenderIdAndTypeOrderByCreatedAtDesc(Long senderId, TransactionType type);
    Optional<Transaction> findByReference(String reference);
}