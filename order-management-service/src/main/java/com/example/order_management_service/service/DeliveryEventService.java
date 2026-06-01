package com.example.order_management_service.service;

import com.example.order_management_service.dto.CityDistanceDto;
import com.example.order_management_service.dto.CourierDelayDto;
import com.example.order_management_service.dto.DeliveryAvgByDayDto;
import com.example.order_management_service.model.DeliveryEvent;
import com.example.order_management_service.repository.DeliveryEventRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventService {

    private final DeliveryEventRepositoryImpl repository;

    @CacheEvict(value = {"deliveryAvgByDay", "topDelayedCouriers", "avgDistanceByCity"}, allEntries = true)
    public void save(DeliveryEvent event) {
        if (event.getTime() == null) {
            event.setTime(Instant.now());
        }
        if (event.getDelayMinutes() == null && event.getDeliveryMinutes() != null && event.getEtaMinutes() != null) {
            event.setDelayMinutes(Math.max(0.0, event.getDeliveryMinutes() - event.getEtaMinutes()));
        }
        repository.save(event);
    }

    public List<DeliveryEvent> findAll() {
        return repository.findAll();
    }

    public List<DeliveryEvent> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId);
    }

    public List<DeliveryEvent> findByCourierId(String courierId) {
        return repository.findByCourierId(courierId);
    }

    @CacheEvict(value = {"deliveryAvgByDay", "topDelayedCouriers", "avgDistanceByCity"}, allEntries = true)
    public boolean deleteByOrderId(String orderId) {
        return repository.deleteByOrderId(orderId);
    }

    @Cacheable(value = "deliveryAvgByDay", key = "#days")
    public List<DeliveryAvgByDayDto> findAvgDeliveryByDay(int days) {
        log.info("[CACHE] Querying InfluxDB for avg delivery by day, days={}", days);
        return repository.findAvgDeliveryByDay(days);
    }

    @Cacheable(value = "topDelayedCouriers", key = "#days + ':' + #limit")
    public List<CourierDelayDto> findTopDelayedCouriers(int days, int limit) {
        log.info("[CACHE] Querying InfluxDB for top delayed couriers, days={}, limit={}", days, limit);
        return repository.findTopDelayedCouriers(days, limit);
    }

    @Cacheable(value = "avgDistanceByCity", key = "#days")
    public List<CityDistanceDto> findAvgDistanceByCity(int days) {
        log.info("[CACHE] Querying InfluxDB for avg distance by city, days={}", days);
        return repository.findAvgDistanceByCity(days);
    }
}
