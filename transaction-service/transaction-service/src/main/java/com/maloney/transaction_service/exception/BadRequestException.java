package com.maloney.transaction_service.exception;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}