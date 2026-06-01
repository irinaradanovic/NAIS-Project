package com.example.order_management_service.repository;

import com.example.order_management_service.dto.CityDistanceDto;
import com.example.order_management_service.dto.CourierDelayDto;
import com.example.order_management_service.dto.DeliveryAvgByDayDto;
import com.example.order_management_service.model.DeliveryEvent;

import java.util.List;

public interface DeliveryEventRepository {
    void save(DeliveryEvent event);
    void saveAll(List<DeliveryEvent> events);
    boolean deleteByOrderId(String orderId);
    List<DeliveryEvent> findAll();
    List<DeliveryEvent> findByOrderId(String orderId);
    List<DeliveryEvent> findByCourierId(String courierId);
    List<DeliveryAvgByDayDto> findAvgDeliveryByDay(int days);
    List<CourierDelayDto> findTopDelayedCouriers(int days, int limit);
    List<CityDistanceDto> findAvgDistanceByCity(int days);
}
