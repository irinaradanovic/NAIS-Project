package com.example.order_management_service.repository;

import com.example.order_management_service.dto.PaymentDeliveryDistanceDto;
import com.example.order_management_service.dto.StatusAvgOrderValueDto;
import com.example.order_management_service.dto.TopCityRevenueDto;
import com.example.order_management_service.model.OrderMetric;

import java.util.List;

public interface OrderMetricRepository {
    void save(OrderMetric metric);
    void saveAll(List<OrderMetric> metrics);
    boolean deleteByOrderId(String orderId);
    List<OrderMetric> findAll();

    List<TopCityRevenueDto> findTopCitiesByRevenue(int limit);
    List<StatusAvgOrderValueDto> findAvgOrderValueByStatus();
    List<PaymentDeliveryDistanceDto> findAvgDeliveryDistanceByPayment();
}
