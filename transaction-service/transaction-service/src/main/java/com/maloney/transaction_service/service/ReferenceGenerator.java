package com.maloney.transaction_service.service;

import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ReferenceGenerator {

    public String generate() {
        return "TXN" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}