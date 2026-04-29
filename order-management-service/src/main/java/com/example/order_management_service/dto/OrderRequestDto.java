package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class OrderRequestDto {
    private LocalDateTime creationDate;
    private String status;
    private String orderType;
    private String address;
}