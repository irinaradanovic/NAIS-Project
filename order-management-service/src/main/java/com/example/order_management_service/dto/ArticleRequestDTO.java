package com.example.order_management_service.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ArticleRequestDTO {
    private String name;
    private Double price;
    private Boolean isAvailable;
}