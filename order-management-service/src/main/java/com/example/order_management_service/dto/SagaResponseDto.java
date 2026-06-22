package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaResponseDto {
    private UUID orderId;
    private String status;
    private String reason;
}
