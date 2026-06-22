package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaItemDto {
    private String menuItemId;
    private Integer quantity;
}
