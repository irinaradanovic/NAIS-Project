package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryStatusResponseDto {
    private String orderId;
    private String status;
    private Double etaMinutes;
    private String courierId;
    private Instant updatedAt;
}
