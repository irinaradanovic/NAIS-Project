package com.example.order_management_service.controller;

import com.example.order_management_service.dto.CreateInvoiceDto;
import com.example.order_management_service.dto.InvoiceResponseDto;
import com.example.order_management_service.dto.UpdateInvoiceDto;
import com.example.order_management_service.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    public InvoiceController(InvoiceService invoiceService) { this.invoiceService = invoiceService; }

    @PostMapping
    public ResponseEntity<InvoiceResponseDto> create(@RequestBody CreateInvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponseDto>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponseDto> update(@PathVariable UUID id, @RequestBody UpdateInvoiceDto dto) {
        return ResponseEntity.ok(invoiceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<InvoiceResponseDto>> getByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(invoiceService.getByOrderId(orderId));
    }
}