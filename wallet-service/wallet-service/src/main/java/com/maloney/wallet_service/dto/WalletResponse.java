package com.maloney.wallet_service.dto;

import com.maloney.wallet_service.entity.WalletStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long id;
    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
    private String currency;
    private WalletStatus status;
    private LocalDateTime createdAt;
}
