package com.example.order_management_service.service;

import com.example.order_management_service.dto.ArticleRevenueByPaymentMethodDto;
import com.example.order_management_service.dto.CreatePaymentDto;
import com.example.order_management_service.dto.PaymentMethodRevenueDto;
import com.example.order_management_service.dto.PaymentResponseDto;
import com.example.order_management_service.dto.PaymentStatusAverageDto;
import com.example.order_management_service.dto.UpdatePaymentDto;
import com.example.order_management_service.model.Payment;
import com.example.order_management_service.repository.OrderRepository;
import com.example.order_management_service.repository.PaymentRepository;
import com.example.order_management_service.repository.PaymentRepositoryAnalytics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepositoryAnalytics paymentRepositoryAnalytics;

    public PaymentResponseDto create(CreatePaymentDto dto) {
        Payment payment = new Payment();
        payment.setMethod(dto.getMethod());
        payment.setStatus(dto.getStatus());
        payment.setAmount(dto.getAmount());
        payment.setPaymentDate(dto.getPaymentDate());

        return mapToDto(paymentRepository.save(payment));
    }

    public List<PaymentResponseDto> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PaymentResponseDto getById(UUID id) {
        return mapToDto(getEntityById(id));
    }

    public PaymentResponseDto update(UUID id, UpdatePaymentDto dto) {
        Payment existing = getEntityById(id);
        if (dto.getMethod() != null) existing.setMethod(dto.getMethod());
        if (dto.getStatus() != null) existing.setStatus(dto.getStatus());
        if (dto.getAmount() != null) existing.setAmount(dto.getAmount());
        if (dto.getPaymentDate() != null) existing.setPaymentDate(dto.getPaymentDate());

        return mapToDto(paymentRepository.save(existing));
    }

    public void delete(UUID id) {
        paymentRepository.deleteById(id);
    }

    public List<PaymentResponseDto> getByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void replacePaymentForOrder(UUID orderId, UUID paymentId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        paymentRepositoryAnalytics.replacePaymentForOrder(orderId, paymentId);
    }

    public Long updatePaymentStatusBeforeDate(String currentStatus, String newStatus, LocalDateTime beforeDate) {
        return paymentRepositoryAnalytics.updatePaymentStatusBeforeDate(currentStatus, newStatus, beforeDate);
    }

    public List<PaymentMethodRevenueDto> getRevenueByPaymentMethod(Double minAmount) {
        return paymentRepositoryAnalytics.findRevenueByPaymentMethod(minAmount)
                .stream()
                .map(row -> new PaymentMethodRevenueDto(
                        row.getMethod(),
                        row.getOrderCount(),
                        row.getTotalRevenue(),
                        row.getAveragePayment()))
                .collect(Collectors.toList());
    }

    public List<PaymentStatusAverageDto> getPaidPaymentAverageByOrderStatus(Long minPayments) {
        return paymentRepositoryAnalytics.findPaidPaymentAverageByOrderStatus(minPayments)
                .stream()
                .map(row -> new PaymentStatusAverageDto(
                        row.getOrderStatus(),
                        row.getPaymentCount(),
                        row.getAverageAmount(),
                        row.getTotalAmount()))
                .collect(Collectors.toList());
    }

    public List<ArticleRevenueByPaymentMethodDto> getArticleRevenueByPaymentMethod(String method) {
        return paymentRepositoryAnalytics.findArticleRevenueByPaymentMethod(method)
                .stream()
                .map(row -> new ArticleRevenueByPaymentMethodDto(
                        row.getArticleName(),
                        row.getOrderCount(),
                        row.getArticleRevenue()))
                .collect(Collectors.toList());
    }

    private Payment getEntityById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    private PaymentResponseDto mapToDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getPaymentDate()
        );
    }
}
