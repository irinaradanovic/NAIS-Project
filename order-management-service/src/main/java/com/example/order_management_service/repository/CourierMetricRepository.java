package com.example.order_management_service.repository;

import com.example.order_management_service.model.CourierMetric;

import java.util.List;

public interface CourierMetricRepository {
    void save(CourierMetric metric);
    void saveAll(List<CourierMetric> metrics);
    boolean deleteByCourierId(String courierId);
    List<CourierMetric> findAll();
    List<CourierMetric> findByCourierId(String courierId);
}
