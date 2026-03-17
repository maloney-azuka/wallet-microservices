package com.maloney.transaction_service.dto;


import com.maloney.transaction_service.entity.TransactionStatus;
import com.maloney.transaction_service.entity.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private String reference;
    private String description;
    private LocalDateTime createdAt;
}