package com.example.order_management_service.dto;

import lombok.Data;
import java.util.UUID;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ArticleResponseDTO {

    private UUID id;
    private String name;
    private Double price;
    private Boolean isAvailable;
}