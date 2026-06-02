package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticleRevenueByPaymentMethodDto {
    private String articleName;
    private Long orderCount;
    private Double articleRevenue;
}
