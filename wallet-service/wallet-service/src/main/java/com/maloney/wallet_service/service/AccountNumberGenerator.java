package com.maloney.wallet_service.service;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class AccountNumberGenerator {

    public String generate() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder("WAL");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}