package com.maloney.transaction_service.feign;


import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {

    @PutMapping("/api/wallets/credit")
    WalletResponse credit(@RequestBody CreditDebitRequest request);

    @PutMapping("/api/wallets/debit")
    WalletResponse debit(@RequestBody CreditDebitRequest request);

    @GetMapping("/api/wallets/balance/{userId}")
    BigDecimal getBalance(@PathVariable Long userId);

    @Data
    class CreditDebitRequest {
        private Long userId;
        private BigDecimal amount;
    }

    @Data
    class WalletResponse {
        private Long id;
        private Long userId;
        private String accountNumber;
        private BigDecimal balance;
        private String currency;
        private String status;
    }
}
