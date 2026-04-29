package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderArticleResponseDto {
    private Long id;
    private UUID orderId;
    private UUID articleId;
    private Integer quantity;
}