package com.maloney.transaction_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotNull(message = "Sender ID is required")
    private Long senderId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;
}