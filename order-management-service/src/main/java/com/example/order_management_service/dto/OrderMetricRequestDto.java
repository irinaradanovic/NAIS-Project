package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMetricRequestDto {
    private String orderId;
    private String status;
    private String orderType;
    private String city;
    private String paymentMethod;
    private Double totalAmount;
    private Integer itemCount;
    private Double deliveryKm;
    private Double discountAmount;
    private Instant time;
}
