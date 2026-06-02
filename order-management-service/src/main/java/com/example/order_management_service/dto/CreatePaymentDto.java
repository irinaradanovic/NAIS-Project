package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CreatePaymentDto {
    private String method;
    private String status;
    private Double amount;
    private LocalDateTime paymentDate;
}
