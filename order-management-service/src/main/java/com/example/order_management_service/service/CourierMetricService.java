package com.example.order_management_service.service;

import com.example.order_management_service.model.CourierMetric;
import com.example.order_management_service.repository.CourierMetricRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourierMetricService {

    private final CourierMetricRepositoryImpl repository;

    public void save(CourierMetric metric) {
        if (metric.getTime() == null) {
            metric.setTime(Instant.now());
        }
        repository.save(metric);
    }

    public List<CourierMetric> findAll() {
        return repository.findAll();
    }

    public List<CourierMetric> findByCourierId(String courierId) {
        return repository.findByCourierId(courierId);
    }

    public boolean deleteByCourierId(String courierId) {
        return repository.deleteByCourierId(courierId);
    }
}
