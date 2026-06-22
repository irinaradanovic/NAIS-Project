package com.example.order_management_service.repository;

import com.example.order_management_service.model.OrderStatusEvent;

import java.util.List;

public interface OrderStatusEventRepository {
    void save(OrderStatusEvent event);
    void saveAll(List<OrderStatusEvent> events);
    boolean deleteByOrderId(String orderId);
    List<OrderStatusEvent> findAll();
}
