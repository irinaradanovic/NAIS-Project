package com.example.order_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InvoiceResponseDto {
    private UUID id;
    private Double price;
    private LocalDate issueDate;
}