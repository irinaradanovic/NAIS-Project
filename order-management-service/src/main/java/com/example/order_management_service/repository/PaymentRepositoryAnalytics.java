package com.example.order_management_service.repository;

import com.example.order_management_service.dto.ArticleRevenueByPaymentMethodDto;
import com.example.order_management_service.dto.PaymentMethodRevenueDto;
import com.example.order_management_service.dto.PaymentStatusAverageDto;
import com.example.order_management_service.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentRepositoryAnalytics {
    List<PaymentMethodRevenueDto> findRevenueByPaymentMethod(Double minAmount);

    List<PaymentStatusAverageDto> findPaidPaymentAverageByOrderStatus(Long minPayments);

    List<ArticleRevenueByPaymentMethodDto> findArticleRevenueByPaymentMethod(String method);

    void deleteById(UUID id);

    void replacePaymentForOrder(UUID orderId, UUID paymentId);

    Long updatePaymentStatusBeforeDate(String currentStatus,
                                       String newStatus,
                                       LocalDateTime beforeDate);
}
