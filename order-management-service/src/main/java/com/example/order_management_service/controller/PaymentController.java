package com.example.order_management_service.controller;

import com.example.order_management_service.dto.ArticleRevenueByPaymentMethodDto;
import com.example.order_management_service.dto.CreatePaymentDto;
import com.example.order_management_service.dto.PaymentMethodRevenueDto;
import com.example.order_management_service.dto.PaymentResponseDto;
import com.example.order_management_service.dto.PaymentStatusAverageDto;
import com.example.order_management_service.dto.UpdatePaymentDto;
import com.example.order_management_service.service.PaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDto> create(@RequestBody CreatePaymentDto dto) {
        return ResponseEntity.ok(paymentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAll() {
        return ResponseEntity.ok(paymentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> update(@PathVariable UUID id, @RequestBody UpdatePaymentDto dto) {
        return ResponseEntity.ok(paymentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getByOrderId(@PathVariable UUID orderId) {
        return ResponseEntity.ok(paymentService.getByOrderId(orderId));
    }

    @PostMapping("/orders/{orderId}/payments/{paymentId}/replace")
    public ResponseEntity<Void> replacePaymentForOrder(@PathVariable UUID orderId, @PathVariable UUID paymentId) {
        paymentService.replacePaymentForOrder(orderId, paymentId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/status-before-date")
    public ResponseEntity<Long> updatePaymentStatusBeforeDate(
            @RequestParam String currentStatus,
            @RequestParam String newStatus,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeDate) {
        return ResponseEntity.ok(paymentService.updatePaymentStatusBeforeDate(currentStatus, newStatus, beforeDate));
    }

    @GetMapping("/analytics/revenue-by-method")
    public ResponseEntity<List<PaymentMethodRevenueDto>> getRevenueByPaymentMethod(
            @RequestParam(defaultValue = "0") Double minAmount) {
        return ResponseEntity.ok(paymentService.getRevenueByPaymentMethod(minAmount));
    }

    @GetMapping("/analytics/average-by-order-status")
    public ResponseEntity<List<PaymentStatusAverageDto>> getPaidPaymentAverageByOrderStatus(
            @RequestParam(defaultValue = "1") Long minPayments) {
        return ResponseEntity.ok(paymentService.getPaidPaymentAverageByOrderStatus(minPayments));
    }

    @GetMapping("/analytics/article-revenue-by-method")
    public ResponseEntity<List<ArticleRevenueByPaymentMethodDto>> getArticleRevenueByPaymentMethod(
            @RequestParam String method) {
        return ResponseEntity.ok(paymentService.getArticleRevenueByPaymentMethod(method));
    }
}
