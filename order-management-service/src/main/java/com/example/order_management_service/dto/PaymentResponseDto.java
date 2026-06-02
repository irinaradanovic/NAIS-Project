package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private UUID id;
    private String method;
    private String status;
    private Double amount;
    private LocalDateTime paymentDate;
}
