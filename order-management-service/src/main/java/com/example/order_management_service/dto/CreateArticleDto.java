package com.example.order_management_service.dto;

import lombok.Data;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CreateArticleDto {
    private UUID orderId;
    private String name;
    private Integer quantity;
    private Double price;
}