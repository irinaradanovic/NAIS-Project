package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusEventRequestDto {
    private String orderId;
    private String status;
    private String channel;
    private String paymentMethod;
    private Double processingSeconds;
    private Double totalAmount;
    private Instant time;
}
