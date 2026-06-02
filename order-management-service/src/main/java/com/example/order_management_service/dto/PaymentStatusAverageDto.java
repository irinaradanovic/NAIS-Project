package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentStatusAverageDto {
    private String orderStatus;
    private Long paymentCount;
    private Double averageAmount;
    private Double totalAmount;
}
