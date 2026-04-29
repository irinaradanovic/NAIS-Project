package com.example.order_management_service.service;

import com.example.order_management_service.dto.CreateInvoiceDto;
import com.example.order_management_service.dto.InvoiceResponseDto;
import com.example.order_management_service.dto.UpdateInvoiceDto;
import com.example.order_management_service.model.Invoice;
import com.example.order_management_service.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    @Autowired private InvoiceRepository invoiceRepository;

    public InvoiceResponseDto create(CreateInvoiceDto dto) {
        Invoice invoice = new Invoice();
        invoice.setPrice(dto.getPrice());
        invoice.setIssueDate(dto.getIssueDate());

        Invoice saved = invoiceRepository.save(invoice);
        return mapToDto(saved);
    }

    public List<InvoiceResponseDto> getAll() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public InvoiceResponseDto getById(UUID id) {
        return mapToDto(getEntityById(id));
    }

    public InvoiceResponseDto update(UUID id, UpdateInvoiceDto dto) {
        Invoice existing = getEntityById(id);
        if (dto.getPrice() != null) existing.setPrice(dto.getPrice());
        if (dto.getIssueDate() != null) existing.setIssueDate(dto.getIssueDate());

        Invoice saved = invoiceRepository.save(existing);
        return mapToDto(saved);
    }

    public void delete(UUID id) {
        invoiceRepository.deleteById(id);
    }

    public List<InvoiceResponseDto> getByOrderId(UUID orderId) {
        return invoiceRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Invoice getEntityById(UUID id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    private InvoiceResponseDto mapToDto(Invoice invoice) {
        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getPrice(),
                invoice.getIssueDate()
        );
    }
}