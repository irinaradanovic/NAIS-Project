package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {
    private LocalDateTime creationDate;
    private String status;
    private String orderType;
    private String address;
    private String restaurantId;
    private List<OrderItemRequestDto> items;
}
