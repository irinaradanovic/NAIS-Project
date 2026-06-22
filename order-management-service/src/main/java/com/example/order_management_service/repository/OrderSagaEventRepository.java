package com.example.order_management_service.repository;

import com.example.order_management_service.model.OrderSagaEvent;

public interface OrderSagaEventRepository {
    void save(OrderSagaEvent event);
}
