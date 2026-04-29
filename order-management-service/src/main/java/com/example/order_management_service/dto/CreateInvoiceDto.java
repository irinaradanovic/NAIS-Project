package com.example.order_management_service.dto;

import lombok.Data;
import java.time.LocalDate;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CreateInvoiceDto {
    private Double price;
    private LocalDate issueDate;
}