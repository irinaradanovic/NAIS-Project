package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private UUID id;
    private LocalDateTime creationDate;
    private String status;
    private String orderType;
    private String address;
}