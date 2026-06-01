package com.example.order_management_service.service;

import com.example.order_management_service.dto.OrderMetricRequestDto;
import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;
import com.example.order_management_service.repository.OrderMetricRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OrderMetricService {

    private final OrderMetricRepository orderMetricRepository;

    public OrderMetricService(OrderMetricRepository orderMetricRepository) {
        this.orderMetricRepository = orderMetricRepository;
    }

    public void create(OrderMetricRequestDto dto) {
        OrderMetric metric = new OrderMetric(
                dto.getOrderId(),
                dto.getStatus(),
                dto.getOrderType(),
                dto.getCity(),
                dto.getPaymentMethod(),
                dto.getTotalAmount(),
                dto.getItemCount(),
                dto.getDeliveryKm(),
                dto.getDiscountAmount(),
                dto.getTime() != null ? dto.getTime() : Instant.now()
        );
        orderMetricRepository.save(metric);
    }

    public List<OrderMetric> getAll() {
        return orderMetricRepository.findAll();
    }

    public boolean deleteByOrderId(String orderId) {
        return orderMetricRepository.deleteByOrderId(orderId);
    }

    public List<TopCityRevenueDto> getTopCitiesByRevenue(int limit) {
        return orderMetricRepository.findTopCitiesByRevenue(limit);
    }

    public List<StatusAvgOrderValueDto> getAvgOrderValueByStatus() {
        return orderMetricRepository.findAvgOrderValueByStatus();
    }

    public List<PaymentDeliveryDistanceDto> getAvgDeliveryDistanceByPayment() {
        return orderMetricRepository.findAvgDeliveryDistanceByPayment();
    }
}
