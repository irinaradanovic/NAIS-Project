package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodRevenueDto {
    private String method;
    private Long orderCount;
    private Double totalRevenue;
    private Double averagePayment;
}
