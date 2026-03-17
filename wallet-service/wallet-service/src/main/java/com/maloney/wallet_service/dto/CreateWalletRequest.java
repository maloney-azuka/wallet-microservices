package com.maloney.wallet_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateWalletRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String currency;
}