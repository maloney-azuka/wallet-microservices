package com.maloney.user_service.feign;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service")
public interface WalletServiceClient {

    @PostMapping("/api/wallets")
    WalletResponse createWallet(@RequestBody CreateWalletRequest request);

    @Data
    class CreateWalletRequest {
        private Long userId;
        private String currency;
    }

    @Data
    class WalletResponse {
        private Long id;
        private Long userId;
        private String accountNumber;
        private String balance;
        private String currency;
        private String status;
    }
}