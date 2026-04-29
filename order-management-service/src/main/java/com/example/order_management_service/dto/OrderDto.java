package com.example.order_management_service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;

@Data
public class OrderDto {
    private LocalDateTime creationDate;
    private String status;
    private String orderType;
    private String address;
}