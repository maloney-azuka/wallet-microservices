package com.maloney.transaction_service.exception;


import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}